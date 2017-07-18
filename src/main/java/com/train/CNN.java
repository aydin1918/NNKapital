package com.train;

import java.io.File;
import java.util.Random;

import org.datavec.api.io.labels.ParentPathLabelGenerator;
import org.datavec.api.split.FileSplit;
import org.datavec.image.loader.NativeImageLoader;
import org.datavec.image.recordreader.ImageRecordReader;
import org.deeplearning4j.datasets.datavec.RecordReaderDataSetIterator;
import org.deeplearning4j.nn.api.OptimizationAlgorithm;
import org.deeplearning4j.nn.conf.MultiLayerConfiguration;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.Updater;
import org.deeplearning4j.nn.conf.inputs.InputType;
import org.deeplearning4j.nn.conf.layers.ConvolutionLayer;
import org.deeplearning4j.nn.conf.layers.DenseLayer;
import org.deeplearning4j.nn.conf.layers.OutputLayer;
import org.deeplearning4j.nn.conf.layers.SubsamplingLayer;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.nn.weights.WeightInit;
import org.deeplearning4j.optimize.listeners.ScoreIterationListener;
import org.deeplearning4j.util.ModelSerializer;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.dataset.api.iterator.DataSetIterator;
import org.nd4j.linalg.dataset.api.preprocessor.DataNormalization;
import org.nd4j.linalg.dataset.api.preprocessor.ImagePreProcessingScaler;
import org.nd4j.linalg.lossfunctions.LossFunctions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



public class CNN {
	/** Data URL for downloading */
    //public static final String DATA_URL = "http://github.com/myleott/mnist_png/raw/master/mnist_png.tar.gz";

    /** Location to save and extract the training/testing data */
    //public static final String DATA_PATH = "C:\\Users\\admin\\workspace\\Image\\img\\Letters"; //FilenameUtils.concat(System.getProperty("java.io.tmpdir"), "dl4j_Mnist/");

    public static final String DATA_PATH = System.getProperty("user.dir"); //FilenameUtils.concat(System.getProperty("java.io.tmpdir"), "dl4j_Mnist/");
    
    private static Logger log = LoggerFactory.getLogger(CNN.class);

    public  void TrainNN() throws Exception {
        // image information
        // 28 * 28 grayscale
        // grayscale implies single channel
        int height = 25;
        int width = 50;
        int channels = 1;
        int rngseed = 123;
        Random randNumGen = new Random(rngseed);
        int batchSize = 16;
        int outputNum = 1;
        int numEpochs = 15;

         /*
        This class downloadData() downloads the data
        stores the data in java's tmpdir
        15MB download compressed
        It will take 158MB of space when uncompressed
        The data can be downloaded manually here
        http://github.com/myleott/mnist_png/raw/master/mnist_png.tar.gz
         */


        //downloadData();

        // Define the File Paths
        File trainData = new File(DATA_PATH + "\\close");
        //File testData = new File(DATA_PATH + "\\mnist_png\\testing");

        

        // Define the FileSplit(PATH, ALLOWED FORMATS,random)

        FileSplit train = new FileSplit(trainData, NativeImageLoader.ALLOWED_FORMATS,randNumGen);
        //FileSplit test = new FileSplit(testData,NativeImageLoader.ALLOWED_FORMATS,randNumGen);

        // Extract the parent path as the image label

        ParentPathLabelGenerator labelMaker = new ParentPathLabelGenerator();

        ImageRecordReader recordReader = new ImageRecordReader(height,width,channels,labelMaker);

        // Initialize the record reader
        // add a listener, to extract the name

        recordReader.initialize(train);
        //recordReader.setListeners(new LogRecordListener());

        // DataSet Iterator

        DataSetIterator dataIter = new RecordReaderDataSetIterator(recordReader,batchSize,1,outputNum);

        // Scale pixel values to 0-1

        DataNormalization scaler = new ImagePreProcessingScaler(0,1);
        scaler.fit(dataIter);
        dataIter.setPreProcessor(scaler);


        // Build Our Neural Network

        log.info("**** Build Model ****");
        
        MultiLayerConfiguration conf = new NeuralNetConfiguration.Builder()
        .seed(rngseed)
        .iterations(1) // Training iterations as above
        .regularization(true).l2(1e-4)
        /*
            Uncomment the following for learning decay and bias
         */
        .learningRate(.001)//.biasLearningRate(0.02)
        //.learningRateDecayPolicy(LearningRatePolicy.Inverse).lrPolicyDecayRate(0.001).lrPolicyPower(0.75)
        .weightInit(WeightInit.XAVIER)
        .optimizationAlgo(OptimizationAlgorithm.STOCHASTIC_GRADIENT_DESCENT)
        .updater(Updater.NESTEROVS).momentum(0.9)
        .list()
        .layer(0, new ConvolutionLayer.Builder(5, 5)
                //nIn and nOut specify depth. nIn here is the nChannels and nOut is the number of filters to be applied
                .nIn(1)
                .stride(1, 1)
                .nOut(8)
                .activation(Activation.RELU)
                .weightInit(WeightInit.XAVIER)
                .build())
        .layer(1, new SubsamplingLayer.Builder(SubsamplingLayer.PoolingType.MAX)
                .kernelSize(2,2)
                .stride(2,2)
                .build())
        .layer(2, new ConvolutionLayer.Builder(5, 5)
                //Note that nIn need not be specified in later layers
                .stride(1, 1)
                .nOut(64)
                .activation(Activation.IDENTITY)
                .build())
        .layer(3, new SubsamplingLayer.Builder(SubsamplingLayer.PoolingType.MAX)
                .kernelSize(2,2)
                .stride(2,2)
                .build())
        .layer(4, new DenseLayer.Builder().activation(Activation.RELU)
                .nOut(128).build())
        .layer(5, new OutputLayer.Builder(LossFunctions.LossFunction.NEGATIVELOGLIKELIHOOD)
                .nOut(outputNum)
                .activation(Activation.SOFTMAX)
                .build())
            .backprop(true).pretrain(false)
            .setInputType(InputType.convolutional(height, width, channels))
            .build();


        MultiLayerNetwork model = new MultiLayerNetwork(conf);
        model.init();

        model.setListeners(new ScoreIterationListener(10));

       // log.info("*****TRAIN MODEL********");
        for(int i = 0; i<numEpochs; i++){
            model.fit(dataIter);
        }

       // log.info("******SAVE TRAINED MODEL******");
        // Details

        // Where to save model
        File locationToSave = new File(DATA_PATH + "\\trained_model_CNN.zip");
        
       // log.info("File exists: " + locationToSave.exists());
        
        // boolean save Updater
        boolean saveUpdater = true;

        // ModelSerializer needs modelname, saveUpdater, Location

        ModelSerializer.writeModel(model,locationToSave,saveUpdater);
    }
}

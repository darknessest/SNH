package jimageprocessor;     //注意！

import com.cyzapps.Jfcalc.ErrProcessor;
import com.cyzapps.Jsma.SMErrProcessor;
import com.cyzapps.SmartMath.SmartCalcProcLib;
import com.cyzapps.imgmatrixproc.ImgNoiseFilter;
import com.cyzapps.imgmatrixproc.ImgThreshBiMgr;
import com.cyzapps.imgproc.ImageMgr;
import com.cyzapps.mathrecog.CharLearningMgr;
import com.cyzapps.mathrecog.ExprRecognizer;
import com.cyzapps.mathrecog.ImageChop;
import com.cyzapps.mathrecog.MisrecogWordMgr;
import com.cyzapps.uptloadermgr.UPTJavaLoaderMgr;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.FileChooser;

import java.awt.image.BufferedImage;
import java.io.*;
import java.net.Socket;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

import static com.cyzapps.SmartCalc.Cut;
import static jimageprocessor.JImageProcessor.recognizeMathExpr;


public class Controller implements Initializable {
    @FXML
    TextField FileAddressField;
    @FXML
    TextFlow CommandFlow;
    @FXML
    AnchorPane AP;

    private String SelectedImagePath = null;
    private String res = null;
    private CharLearningMgr clm;
    private MisrecogWordMgr mwm;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        int nTestMode0PrintRecogMode = ExprRecognizer.RECOG_SPRINT_MODE;
        int nTestMode0HandwritingRecogMode = ExprRecognizer.RECOG_SHANDWRITING_MODE;
        int nTestMode3and4RecogMode = ExprRecognizer.RECOG_SPRINT_MODE;
        boolean bLoadPrintChars = false;
        boolean bLoadSPrintChars = true;
        boolean bLoadSHandwritingChars = true;


        clm = new CharLearningMgr();
        FileInputStream fis = null;
        try {
            fis = new FileInputStream("res" + File.separator + "clm.xml");
            //fis = new FileInputStream("res/clm.xml");
        } catch (FileNotFoundException ex) {
            Logger.getLogger(JImageProcessor.class.getName()).log(Level.SEVERE, null, ex);
        }
        InputStream is = fis;
        if (is != null) {
            clm.readFromXML(is);
            try {
                fis.close();
            } catch (IOException ex) {
                Logger.getLogger(JImageProcessor.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        mwm = new MisrecogWordMgr();
        fis = null;
        try {
            fis = new FileInputStream("res" + File.separator + "mwm.xml");
            //fis = new FileInputStream("res/mwm.xml");
        } catch (FileNotFoundException ex) {
            Logger.getLogger(JImageProcessor.class.getName()).log(Level.SEVERE, null, ex);
        }
        is = fis;
        if (is != null) {
            mwm.readFromXML(is);
            try {
                fis.close();
            } catch (IOException ex) {
                Logger.getLogger(JImageProcessor.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        // load prototypes, this is much quicker as it is coded in source.
        UPTJavaLoaderMgr.load(bLoadPrintChars, bLoadSPrintChars, bLoadSHandwritingChars);

        ExprRecognizer.setRecognitionMode(nTestMode0HandwritingRecogMode); //hand mode on
    }

    @FXML
    private void OpenImage() {
        FileChooser fileChooser = new FileChooser();

        //Set extension filter
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter(
                "image files: bmp, png, jpg",
                "*.bmp", "*.png", "*.jpg"); // more file extensions can be added

        fileChooser.getExtensionFilters().add(extFilter);

        File selectedFile = fileChooser.showOpenDialog(null);

        if (selectedFile != null) {
            ImageView iv = new ImageView(new Image(selectedFile.toURI().toString()));
            iv.setFitWidth(400);
            iv.setFitHeight(250);
            iv.setPreserveRatio(true);
            iv.setSmooth(true);
            iv.setCache(false);

            AP.getChildren().clear();
            AP.getChildren().add(iv);

            SelectedImagePath = selectedFile.getAbsolutePath();
            FileAddressField.setText(SelectedImagePath);
            //TODO PutText 只有第一次调用是正常的，之后均有延迟，（注释掉第一次条用的代码，则只有第二次调用的是正常的）
            PutText(getSelectedImagePath() + " has been opened\n", false, Color.BLACK, "Arial", 16);
        }
    }

    @FXML
    void PreProccess() throws InterruptedException {
        if (getSelectedImagePath() == null) {
            PutText("Please choose a picture or a folder\n", false, Color.BLACK, "Arial", 16);
            return;
        }
        String path = getSelectedImagePath();
        System.out.println(path + " ");//+ pic);
        int nPixelDiv = 100;
        String oldfolder = path.substring(0, path.lastIndexOf(File.separator));
        String pic = path.replace(path.substring(0, path.lastIndexOf(File.separator)), "");
        System.out.println(oldfolder + " " + pic.substring(1, pic.length()));
        String newFolder = "res" + File.separator + "prepresult";
        preprocessImage(pic.substring(1, pic.length()), oldfolder, newFolder, nPixelDiv, true);
        SelectedImagePath = newFolder + File.separator + pic.substring(1, pic.length()) + ".bmp";
        PutText("PreProcess image path: " + SelectedImagePath + "\n", false, Color.BLACK, "Arial", 16);
    }

    @FXML
    void RunProccess() throws InterruptedException {
        if (getSelectedImagePath() == null) {
            PutText("Please choose a picture or a folder\n", false, Color.BLACK, "Arial", 16);
            return;
        }
        String path = getSelectedImagePath();
        res = recognizeMathExpr(path, clm, mwm, true);
        PutText("The recognition result:"+"\n"+res+"\n",false, Color.RED, "Arial", 16);
    }

    @FXML
    void calculate() throws InterruptedException, SMErrProcessor.JSmartMathErrException, ErrProcessor.JFCALCExpErrException {
        if (getRes() == null) {
            PutText("There is no result yet\n", false, Color.BLACK, "Arial", 16);
            return;
        }
        String calcA = null;
        String strExpressions = res;
        if (strExpressions.indexOf("\n") != -1)//方程组
        {
            strExpressions = Cut(strExpressions);
        } else if (strExpressions.indexOf("integrate") != -1 && strExpressions.indexOf("==") != -1)//积分方程
        {
            String calcA1;
            String[] strarraycup = strExpressions.split("==");
            calcA1 = SmartCalcProcLib.calculate(strarraycup[0], false);
            String temp = calcA1.replace(File.separator + "text", "");//改了\\
            temp = temp.replace("\"", "");
            temp = temp.replace("{", "(");
            temp = temp.replace("}", ")");
            System.out.println(temp);
            temp = temp.replace("×", "*");
            temp = temp.replace("^", "**");
            strExpressions = temp + "==" + strarraycup[1];
        }
        if (strExpressions.indexOf("derivative") != -1) {//求导
            Function df = new Function(strExpressions);
            String arg = df.x + String.valueOf(df.ccount) + df.str;//这里的df.ccount，为求导阶数，目前一阶导数测试通过
            try {
                Socket socket = new Socket("127.0.0.1", 9999);
                System.out.println("Client start!");
                PrintWriter out = new PrintWriter(socket.getOutputStream()); // 输出，to 服务器 socket
                out.println("derivative:" + arg);
                out.flush(); // 刷缓冲输出，to 服务器

                BufferedReader in = new BufferedReader(new InputStreamReader(
                        socket.getInputStream())); // 输入， from 服务器 socket
                calcA = in.readLine();
                System.out.println("Client end!");
                socket.close();
                //boolean success = (new File(dir)).delete();
            } catch (UnknownHostException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            calcA = SmartCalcProcLib.calculate(strExpressions, false);
        }

        String temp = calcA.replace(File.separator + "text", "");
        temp = temp.replace("\"", "");
        temp = temp.replace("{", "(");
        temp = temp.replace("}", ")");
        if(temp.length()==0)
            temp="I can calculate it yet";
        PutText("ANWSER:\n"+temp + "\n\n", false, Color.BLACK, "Arial", 16);
        System.out.println(temp);
    }

    public String getRes() {
        return res;
    }

    //  This method can be used outside (in other files)
    //  to add text to the CommandFlow (right)
    // clearField == true, CommandFlow we'll be cleared
    public void PutText(String text, boolean clearField, Color color, String fontName, int size) {
        if (clearField)
            CommandFlow.getChildren().clear();

        Text caption = new Text(text);
        caption.setFont(Font.font(fontName, size));
        caption.setFill(color);
        CommandFlow.getChildren().add(caption);
    }

    // This method can be used outside (in other files)
    // Returns path to the opened image
    // TODO 如果在别的文档不能用这个函数，就可以删掉
    public String getSelectedImagePath() {
        return SelectedImagePath;
    }

    public static class Function {
        public static String str;
        public static String x;
        public static int ccount;

        public Function(String Ostr) {
            int[] count = new int[6];
            count[0] = Ostr.indexOf("\"");
            for (int i = 1; i < 6; i++) {
                count[i] = Ostr.indexOf("\"", count[i - 1] + 1);
            }
            str = Ostr.substring(count[0] + 1, count[1]);
            x = Ostr.substring(count[2] + 1, count[3]);
            ccount = 1;
        }
        // TODO 计算求导阶数，并赋值给ccount
        public static void StringCount(String str) {
            int index = 0;
            String key = "derivative";
            while ((index = str.indexOf(key)) != -1) {
                str = str.substring(index + key.length());
                ccount++;
            }

        }
    }

    public static byte[][] preprocessImage(String strImageFile, String strSrcFolder, String strDestFolder, int nPixelDiv, boolean bFilterSmooth) throws InterruptedException {
        System.out.println("Now processing image file " + strImageFile);
        if (bFilterSmooth) {
            BufferedImage image = ImageMgr.readImg(strSrcFolder + File.separator + strImageFile);
            int[][] grayMatrix = ImageMgr.convertImg2GrayMatrix(image);
            BufferedImage image_grayed = ImageMgr.convertGrayMatrix2Img(grayMatrix);
            ImageMgr.saveImg(image_grayed, "mr_grayed.bmp");

            grayMatrix = ImgNoiseFilter.filterNoiseNbAvg4Gray(grayMatrix, 1);
            BufferedImage image_filtered = ImageMgr.convertGrayMatrix2Img(grayMatrix);
            ImageMgr.saveImg(image_filtered, "mr_filtered.bmp");

            int nWHMax = Math.max(grayMatrix.length, grayMatrix[0].length);
            int nEstimatedStrokeWidth = (int) Math.ceil((double) nWHMax / (double) nPixelDiv);
            byte[][] biMatrix = ImgThreshBiMgr.convertGray2Bi2ndD(grayMatrix, (int) Math.max(3.0, nEstimatedStrokeWidth / 2.0));  // selected value was 6.
            BufferedImage image_bilized1 = ImageMgr.convertBiMatrix2Img(biMatrix);
            ImageMgr.saveImg(image_bilized1, "mr_bilized1.bmp");
            ImageChop imgChop = new ImageChop();
            imgChop.setImageChop(biMatrix, 0, 0, biMatrix.length, biMatrix[0].length, ImageChop.TYPE_UNKNOWN);
            double dAvgStrokeWidth = imgChop.calcAvgStrokeWidth();

            int nFilterR = (int) Math.ceil((dAvgStrokeWidth / 2.0 - 1) / 2.0);
            biMatrix = ImgNoiseFilter.filterNoiseNbAvg4Bi(biMatrix, nFilterR, 1);
            biMatrix = ImgNoiseFilter.filterNoiseNbAvg4Bi(biMatrix, nFilterR, 2);
            imgChop.setImageChop(biMatrix, 0, 0, biMatrix.length, biMatrix[0].length, ImageChop.TYPE_UNKNOWN);
            BufferedImage image_smoothed1 = ImageMgr.convertBiMatrix2Img(imgChop.mbarrayImg);
            ImageMgr.saveImg(image_smoothed1, "mr_smoothed1.bmp");

            biMatrix = ImgNoiseFilter.filterNoisePoints4Bi(biMatrix, (int) dAvgStrokeWidth);
            imgChop.setImageChop(biMatrix, 0, 0, biMatrix.length, biMatrix[0].length, ImageChop.TYPE_UNKNOWN);
            BufferedImage image_smoothed2 = ImageMgr.convertBiMatrix2Img(imgChop.mbarrayImg);
            ImageMgr.saveImg(image_smoothed2, "mr_smoothed2.bmp");
            ImageMgr.saveImg(image_smoothed2, strDestFolder + File.separator + strImageFile + ".bmp");
            return biMatrix;
        } else {
            BufferedImage image = ImageMgr.readImg(strSrcFolder + File.separator + strImageFile);
            byte[][] biMatrix = ImageMgr.convertImg2BiMatrix(image);
            return biMatrix;
        }
    }


}
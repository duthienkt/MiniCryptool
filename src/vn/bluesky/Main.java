package vn.bluesky;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import vn.bluesky.crypt.CryptResult;
import vn.bluesky.crypt.huf.HT_Decryptor;
import vn.bluesky.crypt.huf.HT_Encryptor;
import vn.bluesky.crypt.huf.HT_KeyGen;
import vn.bluesky.crypt.rsa.RSA_Decryptor;
import vn.bluesky.crypt.rsa.RSA_Encryptor;
import vn.bluesky.crypt.rsa.RSA_KeyGen;
import vn.bluesky.crypt.sc.SC_Decryptor;
import vn.bluesky.crypt.sc.SC_Encryptor;
import vn.bluesky.crypt.sc.SC_KeyGen;
import vn.bluesky.ui.DialogBox;
import vn.bluesky.utils.data.ArrayUtil;

import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.File;

public class Main extends Application {
    public static String hr = "===================================\n";
    TabPane root;
    Tab subCipTab;
    Tab rsaTab;
    Tab htTab;

    Font textF = Font.font("Consolas", FontWeight.NORMAL, 20);
    TextField sc_key;
    SC_KeyGen sc_keyGen;
    TextField sc_inputPath;
    TextArea sc_outputMessage;


    RSA_KeyGen rsa_keyGen;
    TextField rsa_inputPath;
    TextArea rsa_outputMessage;


    TextField ht_inputPath;
    TextArea ht_outputMessage;
    int[] inputHis = null;
    HT_KeyGen.BNode ht_tree = null;

    public static void main(String[] args) {
        // test();
        launch(args);
    }

    static void test() {
//        BigInteger e = new BigInteger(new byte[]{1,2,3,4});
//        BigInteger f = new BigInteger()
//        System.out.println(ArrayUtil.bytes2Hex(e.toByteArray())+ " ---"+ e.bitLength());
////        BitC fi = BitC.openToRead("E:/temp/a.mp4");
//        BitC fo = BitC.openToWrite("E:/temp/b.mp4");
//        long c = fi.remain();
//        while (fi.remain()>0)
//        {
//            fo.writeBit(fi.readBit());
//
//        }
//        fi.close();
//        fo.close();
//          Heap<FakeNode> h = new Heap<>(FakeNode.class, 10000);
//          for (int i = 0; i< 10000; ++i)
//              h.push(new FakeNode(i));
//          int c = 10000;
//          while (h.size()>0)
//          {
//              if ((--c)!= h.top().value) break;
//              System.out.println(h.top().value);
//              h.removeTop();
//          }


    }

    @Override
    public void start(Stage primaryStage) {
        //primaryStage.getIcons().add(new Image("file:icon.png"));
        primaryStage.setTitle("Mini Cryptool");
        root = new TabPane();
        root.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        Scene scene = new Scene(root, 800, 500);
        primaryStage.setScene(scene);
        primaryStage.show();
        createPage1();
        createPage2();
        createPage3();

    }

    void createPage1() {
        subCipTab = new Tab();
        subCipTab.setText("Substitution Cipher");
        GridPane gridPane = new GridPane();
        subCipTab.setContent(gridPane);
        gridPane.setHgap(10);
        gridPane.setVgap(10);
        gridPane.setPadding(new Insets(25, 25, 25, 25));
        Text text = new Text("Substitution Cipher");
        text.setFont(textF);
        gridPane.add(text, 1, 1, 4, 1);
        Label label = new Label("Key");
        gridPane.add(label, 2, 2);
        sc_key = new TextField();
        sc_key.setMinWidth(400);
        sc_key.setEditable(false);
        gridPane.add(sc_key, 3, 2);
        Button button = new Button();
        button.setText("Generate");
        button.setOnMouseClicked(event -> {
            long startTime = System.currentTimeMillis();
            sc_outputMessage.appendText(hr + "Generate key\n");
            sc_keyGen = new SC_KeyGen();
            sc_outputMessage.appendText("Time : " + (System.currentTimeMillis() - startTime) + "(ms)\n");
            sc_key.setText(ArrayUtil.bytes2Hex(sc_keyGen.getTransformation()));
        });
        gridPane.add(button, 4, 2);

        button = new Button();
        button.setText("Import");
        button.setOnMouseClicked(event -> {
            //todo import file  key
            DialogBox.chooseFileDialog("Open", result -> {
                        SC_KeyGen.fromFile(result, res -> {
                            sc_keyGen = res;
                            sc_key.setText(ArrayUtil.bytes2Hex(sc_keyGen.getTransformation()));
                        }, null);
                    },
                    null,
                    new FileNameExtensionFilter("Substitution Cipher key(*.sck)", "sck"));
        });
        gridPane.add(button, 5, 2);

        button = new Button();
        button.setText("Export");
        button.setOnMouseClicked(event -> {
            //todo import file  key
            if (sc_keyGen == null) return;
            DialogBox.chooseFileDialog("Save", result -> {
                        DialogBox.passwordCreateDialog("Encrypt you key", result1 ->
                                {
                                    String p = result;
                                    if (!p.endsWith(".sck")) p += ".sck";
                                    sc_keyGen.saveToFile(p, result1);
                                },
                                null);
                    },
                    null,
                    new FileNameExtensionFilter("Substitution Cipher key(*.sck)", "sck"));
        });
        gridPane.add(button, 6, 2);
        label = new Label("File input");
        gridPane.add(label, 2, 3);
        sc_inputPath = new TextField();
        sc_inputPath.setMinWidth(400);
        gridPane.add(sc_inputPath, 3, 3);
        root.getTabs().add(subCipTab);
        button = new Button();
        button.setText("...");
        button.setOnMouseClicked(event -> {
            DialogBox.chooseFileDialog("Open", result -> {
                sc_inputPath.setText(result);

            }, null, null);
        });
        gridPane.add(button, 4, 3);
        sc_outputMessage = new TextArea();

        sc_outputMessage.setMinSize(600, 200);
        gridPane.add(sc_outputMessage, 2, 5, 5, 1);
        button = new Button("Encrypt");
        gridPane.add(button, 3, 4);
        button.setOnMouseClicked(event -> {
            if (sc_keyGen == null) {
                sc_outputMessage.appendText("Generate a new key or import it!\n");
                return;
            }

            if (sc_inputPath.getText().length() == 0) {
                sc_outputMessage.appendText("You must choose a file!\n");
                return;
            }

            if (!(new File(sc_inputPath.getText()).exists())) {
                sc_outputMessage.appendText("File not found!\n");
                return;
            }
            sc_outputMessage.appendText(hr);
            sc_outputMessage.appendText("Encrypting..\n");
            SC_Encryptor encryptor = new SC_Encryptor(sc_keyGen.getTransformation());
            CryptResult res = encryptor.encryptFile(sc_inputPath.getText(), sc_inputPath.getText() + ".sce");
            if (res.isOK) {
                sc_outputMessage.appendText("Encrypted : " + res.sizeOfData + " bytes\nTime :" + res.time + "(ms)\n");
                sc_outputMessage.appendText("Save to : " + sc_inputPath.getText() + ".sce\n");

            } else {
                sc_outputMessage.appendText("Encrypt fail!\n");
            }
        });
        button = new Button("Decrypt");
        button.setOnMouseClicked(event -> {
            if (sc_keyGen == null) {
                sc_outputMessage.appendText("Generate a new key or import it!\n");
                return;
            }

            if (sc_inputPath.getText().length() == 0) {
                sc_outputMessage.appendText("You must choose a file!\n");
                return;
            }

            if (!(new File(sc_inputPath.getText()).exists())) {
                sc_outputMessage.appendText("File not found!\n");
                return;
            }

            if (!sc_inputPath.getText().endsWith("sce")) {
                sc_outputMessage.appendText("Can't decrypt this file!\n");
                return;
            }
            sc_outputMessage.appendText(hr);
            sc_outputMessage.appendText("Decrypting..\n");
            String p = sc_inputPath.getText();
            String op = p.substring(0, p.length() - 8) + "_"
                    + System.currentTimeMillis() / 2000 + p.substring(p.length() - 8, p.length() - 4);
            CryptResult res = new SC_Decryptor(sc_keyGen.getTransformation()).decryptFile(p, op);
            if (res.isOK) {
                sc_outputMessage.appendText("Decrypted : " + res.sizeOfData + " bytes\nTime :" + res.time + "(ms)\n");
                sc_outputMessage.appendText("Save to : " + op + "\n");

            } else {
                sc_outputMessage.appendText("Can't decrypt this file or key isn't match!\n");
            }


        });
        gridPane.add(button, 4, 4);
    }

    void createPage2() {
        rsaTab = new Tab();
        rsaTab.setText("RSA");
        root.getTabs().add(rsaTab);
        GridPane gridPane = new GridPane();
        rsaTab.setContent(gridPane);
        gridPane.setHgap(10);
        gridPane.setVgap(10);
        gridPane.setPadding(new Insets(25, 25, 25, 25));
        Text text = new Text("RSA");
        text.setFont(textF);
        gridPane.add(text, 1, 1, 4, 1);
        Label label = new Label("n");
        gridPane.add(label, 2, 2);
        TextField nTf = new TextField();
        nTf.setEditable(false);
        nTf.setMinWidth(400);
        gridPane.add(nTf, 3, 2, 4, 1);

        label = new Label("e");
        gridPane.add(label, 2, 3);
        TextField eTf = new TextField();
        eTf.setMinWidth(400);
        eTf.setEditable(false);
        gridPane.add(eTf, 3, 3, 4, 1);

        label = new Label("d");
        gridPane.add(label, 2, 4);
        TextField dTf = new TextField();
        dTf.setMinWidth(400);
        dTf.setEditable(false);
        gridPane.add(dTf, 3, 4, 4, 1);

        Button button = new Button("Import");
        button.setOnMouseClicked(event -> {
            DialogBox.chooseFileDialog("Open", result -> {
                        RSA_KeyGen.fromFile(result, res -> {
                            rsa_keyGen = res;
                            nTf.setText(rsa_keyGen.n.toString());
                            eTf.setText(rsa_keyGen.e.toString());
                            dTf.setText(rsa_keyGen.d.toString());
                        }, null);
                    },
                    null,
                    new FileNameExtensionFilter("RSA key(*.rsk)", "rsk"));
        });
        gridPane.add(button, 3, 5);

        button = new Button("Export");
        button.setOnMouseClicked(event -> {
            if (rsa_keyGen == null) return;
            DialogBox.chooseFileDialog("Save", result -> {
                        DialogBox.passwordCreateDialog("Encrypt you key", result1 ->
                                {
                                    String p = result;
                                    if (!p.endsWith(".rsk")) p += ".rsk";
                                    rsa_keyGen.saveToFile(p, result1);
                                },
                                null);
                    },
                    null,
                    new FileNameExtensionFilter("RSA key(*.rsk)", "rsk"));
        });
        gridPane.add(button, 4, 5);

        button = new Button("Generate");
        gridPane.add(button, 5, 5);
        button.setOnMouseClicked(event -> {
            rsa_outputMessage.appendText(hr);
            rsa_outputMessage.appendText("Generate key\n");
            long startTime = System.currentTimeMillis();
            rsa_keyGen = RSA_KeyGen.generateKeys(64 * 8);
            rsa_outputMessage.appendText("Time : " + (System.currentTimeMillis() - startTime) + "(ms)\n");

            nTf.setText(rsa_keyGen.n.toString());
            eTf.setText(rsa_keyGen.e.toString());
            dTf.setText(rsa_keyGen.d.toString());
        });

        label = new Label("File input");
        gridPane.add(label, 2, 6);
        rsa_inputPath = new TextField();
        rsa_inputPath.setMinWidth(400);
        gridPane.add(rsa_inputPath, 3, 6, 3, 1);
        button = new Button("...");
        button.setOnMouseClicked(event -> {
            DialogBox.chooseFileDialog("Open", result -> {
                rsa_inputPath.setText(result);
            }, null, null);
        });
        gridPane.add(button, 6, 6);
        button = new Button("Encrypt");
        button.setOnMouseClicked(event -> {
            if (rsa_keyGen == null) {
                rsa_outputMessage.appendText("Generate a new key or import it!\n");
                return;
            }

            if (rsa_inputPath.getText().length() == 0) {
                rsa_outputMessage.appendText("You must choose a file!\n");
                return;
            }

            if (!(new File(rsa_inputPath.getText()).exists())) {
                rsa_outputMessage.appendText("File not found!\n");
                return;
            }
            String p = rsa_inputPath.getText();
            String po = p + ".rse";
            RSA_Encryptor encryptor = new RSA_Encryptor(rsa_keyGen.bitLength / 8, rsa_keyGen.n, rsa_keyGen.e);
            rsa_outputMessage.appendText(hr);
            CryptResult res = encryptor.encryptFile(p, po);
            if (res.isOK) {
                rsa_outputMessage.appendText("Encrypted : " + res.sizeOfData + " bytes\nTime :" + res.time + "(ms)\n");
                rsa_outputMessage.appendText("Save to : " + po + "\n");

            } else {
                rsa_outputMessage.appendText("Encrypt fail!\n");
            }
        });
        gridPane.add(button, 3, 7);
        button = new Button("Decrypt");
        button.setOnMouseClicked(event -> {
            if (rsa_keyGen == null) {
                rsa_outputMessage.appendText("Generate a new key or import it!\n");
                return;
            }

            if (rsa_inputPath.getText().length() == 0) {
                rsa_outputMessage.appendText("You must choose a file!\n");
                return;
            }

            if (!(new File(rsa_inputPath.getText()).exists())) {
                rsa_outputMessage.appendText("File not found!\n");
                return;
            }

            if (!rsa_inputPath.getText().endsWith("rse")) {
                rsa_outputMessage.appendText("Can't decrypt this file!\n");
                return;
            }
            String p = rsa_inputPath.getText();
            String op = p.substring(0, p.length() - 8) + "_"
                    + System.currentTimeMillis() / 2000 + p.substring(p.length() - 8, p.length() - 4);

            rsa_outputMessage.appendText(hr);
            CryptResult res = new RSA_Decryptor(rsa_keyGen.bitLength / 8, rsa_keyGen.n, rsa_keyGen.d).decryptFile(p, op);
            if (res.isOK) {
                rsa_outputMessage.appendText("Decrypted : " + res.sizeOfData + " bytes\nTime :" + res.time + "(ms)\n");
                rsa_outputMessage.appendText("Save to : " + op + "\n");

            } else {
                rsa_outputMessage.appendText("Can't decrypt this file or key isn't match!\n");
            }

        });
        gridPane.add(button, 5, 7);

        rsa_outputMessage = new TextArea();
        gridPane.add(rsa_outputMessage, 2, 8, 5, 1);
    }


    void createPage3() {
        htTab = new Tab();
        htTab.setText("Huffman tree");
        root.getTabs().add(htTab);
        GridPane gridPane = new GridPane();
        htTab.setContent(gridPane);
        gridPane.setHgap(10);
        gridPane.setVgap(10);
        gridPane.setPadding(new Insets(25, 25, 25, 25));
        Text text = new Text("Huffman Tree");
        text.setFont(textF);
        gridPane.add(text, 1, 0, 4, 1);
        final NumberAxis xAxis = new NumberAxis();
        final NumberAxis yAxis = new NumberAxis();
        LineChart<Number, Number> lineChart = new LineChart<Number, Number>(xAxis, yAxis);
        lineChart.setCreateSymbols(false);
        gridPane.add(lineChart, 2, 1, 6, 1);
        lineChart.setTitle("N/A");
        lineChart.setMaxHeight(200);
        Label label = new Label("Tree");
        gridPane.add(label, 2, 2);
        final TextField treeTf = new TextField();
        treeTf.setEditable(false);
        gridPane.add(treeTf, 3, 2, 6, 1);
        Button button = new Button("Generate");
        button.setOnMouseClicked(event -> {
            DialogBox.chooseFileDialog("Open", result -> {

                ht_outputMessage.appendText(hr);
                ht_outputMessage.appendText("Generate key\n");
                long timex = System.currentTimeMillis();
                inputHis = HT_KeyGen.histogramOfFile(result);
                ht_tree = HT_KeyGen.buildTree(inputHis);
                treeTf.setText(ht_tree.toString());
                ht_outputMessage.appendText("Time : " + (System.currentTimeMillis() - timex) + "(ms)\n");
                lineChart.getData().clear();
                lineChart.setTitle(result);

                lineChart.setTitle(ht_inputPath.getText());
                lineChart.getData().add(createHistogramSeries(ht_inputPath.getText(), inputHis));

            }, null, null);
        });
        gridPane.add(button, 3, 3);
        button = new Button("Import");
        button.setOnMouseClicked(event -> {
            DialogBox.chooseFileDialog("Open", result -> {
                        HT_KeyGen.fromFile(result, res -> {
                            lineChart.getData().clear();
                            lineChart.setTitle("N/A");
                            ht_tree = res;
                            treeTf.setText(ht_tree.toString());
                        }, null);
                    },
                    null,
                    new FileNameExtensionFilter("Huffman tree(*.htk)", "htk"));
        });
        gridPane.add(button, 5, 3);
        button = new Button("Export");
        button.setOnMouseClicked(event -> {
            if (ht_tree == null) return;
            DialogBox.chooseFileDialog("Save", result -> {
                        DialogBox.passwordCreateDialog("Encrypt you key", result1 ->
                                {
                                    String p = result;
                                    if (!p.endsWith(".htk")) p += ".htk";
                                    HT_KeyGen.saveToFile(ht_tree, p, result1);
                                },
                                null);
                    },
                    null,
                    new FileNameExtensionFilter("Huffman tree(*.htk)", "htk"));
        });
        gridPane.add(button, 7, 3);
        label = new Label("File input");
        gridPane.add(label, 2, 4);
        ht_inputPath = new TextField();
        ht_inputPath.setMinWidth(400);
        gridPane.add(ht_inputPath, 3, 4, 5, 1);
        button = new Button("...");
        button.setOnMouseClicked(event -> {
            DialogBox.chooseFileDialog("Open", result -> {
                ht_inputPath.setText(result);
            }, null, null);
        });
        gridPane.add(button, 8, 4);
        button = new Button("Encrypt");
        button.setOnMouseClicked(event -> {
            if (ht_tree == null) {
                ht_outputMessage.appendText("Generate a new key or import it!\n");
                return;
            }

            if (ht_inputPath.getText().length() == 0) {
                ht_outputMessage.appendText("You must choose a file!\n");
                return;
            }

            if (!(new File(ht_inputPath.getText()).exists())) {
                ht_outputMessage.appendText("File not found!\n");
                return;
            }
            String p = ht_inputPath.getText();
            String po = p + ".hte";
            HT_Encryptor encryptor = new HT_Encryptor(ht_tree);
            ht_outputMessage.appendText(hr);
            CryptResult res = encryptor.encryptFile(p, po);
            if (res.isOK) {
                ht_outputMessage.appendText("Encrypted : " + res.sizeOfData + " bytes\nTime :" + res.time + "(ms)\n");
                ht_outputMessage.appendText("Save to : " + po + "\n");

            } else {
                ht_outputMessage.appendText("Encrypt fail!\n");
            }
        });
        gridPane.add(button, 3, 5);
        button = new Button("Decrypt");
        gridPane.add(button, 5, 5);
        button.setOnMouseClicked(event -> {
            if (ht_tree == null) {
                ht_outputMessage.appendText("Generate a new key or import it!\n");
                return;
            }

            if (ht_inputPath.getText().length() == 0) {
                ht_outputMessage.appendText("You must choose a file!\n");
                return;
            }

            if (!(new File(ht_inputPath.getText()).exists())) {
                ht_outputMessage.appendText("File not found!\n");
                return;
            }

            if (!ht_inputPath.getText().endsWith("hte")) {
                ht_outputMessage.appendText("Can't decrypt this file!\n");
                return;
            }
            String p = ht_inputPath.getText();
            String op = p.substring(0, p.length() - 8) + "_"
                    + System.currentTimeMillis() / 2000 + p.substring(p.length() - 8, p.length() - 4);

            ht_outputMessage.appendText(hr);
            CryptResult res = new HT_Decryptor(ht_tree).decryptFile(p, op);
            if (res.isOK) {
                ht_outputMessage.appendText("Decrypted : " + res.sizeOfData + " bytes\nTime :" + res.time + "(ms)\n");
                ht_outputMessage.appendText("Save to : " + op + "\n");

            } else {
                ht_outputMessage.appendText("Can't decrypt this file or key isn't match!\n");
            }

        });
        button = new Button("Generate key from input");
        button.setOnMouseClicked(event -> {
            if (ht_inputPath.getText().length() == 0) {
                ht_outputMessage.appendText("You must choose a file!\n");
                return;
            }

            if (!(new File(ht_inputPath.getText()).exists())) {
                ht_outputMessage.appendText("File not found!\n");
                return;
            }
            ht_outputMessage.appendText(hr);
            ht_outputMessage.appendText("Generate key\n");
            long timex = System.currentTimeMillis();
            inputHis = HT_KeyGen.histogramOfFile(ht_inputPath.getText());
            ht_tree = HT_KeyGen.buildTree(inputHis);
            ht_outputMessage.appendText("Time : " + (System.currentTimeMillis() - timex) + "(ms)\n");
            treeTf.setText(ht_tree.toString());
            lineChart.getData().clear();
            lineChart.setTitle(ht_inputPath.getText());
            lineChart.getData().add(createHistogramSeries(ht_inputPath.getText(), inputHis));
        });
        gridPane.add(button, 7, 5);

        ht_outputMessage = new TextArea();
        gridPane.add(ht_outputMessage, 2, 6, 7, 1);
//        //defining a series
//        int [] h = HT_KeyGen.histogramOfFile("C:\\Users\\DuThien\\Documents\\a2.cpp");
//        HT_KeyGen.BNode ht_tree = HT_KeyGen.buildTree(h);
//        byte []data = ht_tree.toArray();
//        HT_KeyGen.BNode tree2 = new HT_KeyGen.BNode(data);
//        System.out.println(ht_tree.toString());
//        System.out.println(tree2.toString());
//        System.out.println(tree2.toString().equals(ht_tree.toString()));
//        boolean [][] dic = ht_tree.getDictionary();
//        for (int i = 0; i< 256; ++i)
//        {
//            System.out.print((i-128)+ "["+ (dic[i][0]?1:0)+",");
//            for (int j = 1; j< dic[i].length; ++j)
//                System.out.print(","+ (dic[i][j]?1:0));
//            System.out.println("]");
//        }
//        HT_Encryptor encryptor = new HT_Encryptor(ht_tree);
//        encryptor.encryptFile("C:\\Users\\DuThien\\Documents\\a2.cpp",
//                "C:\\Users\\DuThien\\Documents\\a2.cpp.hte");
//        lineChart.getData().clear();
//        lineChart.getData().add(createHistogramSeries("Histogram",h));
    }

    private XYChart.Series createHistogramSeries(String name, int[] his) {
        XYChart.Series series = new XYChart.Series();
        series.setName(name);
        for (int i = 0; i < his.length; ++i)
            series.getData().add(new XYChart.Data(i - 128, his[i]));
        return series;
    }


}

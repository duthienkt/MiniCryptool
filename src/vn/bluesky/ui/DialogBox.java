package vn.bluesky.ui;

import javafx.application.Platform;
import javafx.scene.control.Alert;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;


/**
 * Created by DuThien on 22/03/2017.
 */
public class DialogBox {
    private static Object mutex = new Object();
    private static JFileChooser fileChooser = null;

    public static void chooseFileDialog(String action, OnReturnStringCallback selectedCallback, OnCancelCallback cancelCallback, FileFilter filter) {
        new Thread(() -> {
            synchronized (mutex) {
                if (fileChooser == null) fileChooser = new JFileChooser();
                fileChooser.resetChoosableFileFilters();
                fileChooser.setFileFilter(filter);
                int retVal = fileChooser.showDialog(null, action);
                if (retVal == JFileChooser.APPROVE_OPTION) {
                    Platform.runLater(() -> selectedCallback.onResult(fileChooser.getSelectedFile().getPath()));
                } else {
                    if (cancelCallback != null)
                        Platform.runLater(() -> cancelCallback.onCancel());
                }
            }
        }).run();

    }

    public static void passwordDialog(String title,OnReturnStringCallback selectedCallback, OnCancelCallback cancelCallback) {
       // new Thread(() -> {
            JPanel panel = new JPanel();
            JLabel label = new JLabel("Enter a password:");
            JPasswordField pass = new JPasswordField(10);
            panel.add(label);
            panel.add(pass);
            String[] options = new String[]{"Cancel", "OK"};
            int option = JOptionPane.showOptionDialog(null, panel, title,
                    JOptionPane.NO_OPTION, JOptionPane.PLAIN_MESSAGE,
                    null, options, options[1]);
            if (option == 1) // pressing OK button
            {
                char[] password = pass.getPassword();
                Platform.runLater(() -> selectedCallback.onResult(new String(password)));
            } else if (cancelCallback != null)
                Platform.runLater(() -> cancelCallback.onCancel());

        //}).run();

    }
    public static void passwordCreateDialog(String title,OnReturnStringCallback selectedCallback, OnCancelCallback cancelCallback) {
        //new Thread(() -> {
            JPanel panel = new JPanel();
            JLabel label = new JLabel("Type a password:");
            JPasswordField pass = new JPasswordField(10);
            panel.add(label);
            panel.add(pass);
            JLabel label1 = new JLabel("Retype password:");

            JPasswordField pass1 = new JPasswordField(10);
            panel.add(label1);
            panel.add(pass1);


            String[] options = new String[]{ "Cancel", "OK"};
            int option = JOptionPane.showOptionDialog(null, panel, title,
                    JOptionPane.NO_OPTION, JOptionPane.PLAIN_MESSAGE,
                    null, options, options[1]);
            if (option == 1) // pressing OK button
            {
                if (new String(pass.getPassword()).length()>0&&new String(pass.getPassword()).equals(new String(pass1.getPassword()))) {
                    char[] password = pass.getPassword();
                    Platform.runLater(() -> selectedCallback.onResult(new String(password)));
                }
                else
                    passwordCreateDialog(title, selectedCallback, cancelCallback);
            } else if (cancelCallback != null)
                Platform.runLater(() -> cancelCallback.onCancel());

        //}).run();

    }




    public static void showAlert(String title, String mess)
    {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(mess);
        alert.showAndWait();
    }

    public interface OnReturnStringCallback {
        void onResult(String result);
    }

    public interface OnCancelCallback {
        void onCancel();
    }

}

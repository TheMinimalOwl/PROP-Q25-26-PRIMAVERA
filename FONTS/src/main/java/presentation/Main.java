package presentation;

import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                CtrlPresentation.getInstance().close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }));

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                CtrlPresentation.getInstance().inicializarPresentacion();
            }
        });
    }
}

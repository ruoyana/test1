package everyclientonefile.client1;

import java.io.File;

public class Client {

    private static String filename = "D:\\JavaLearn";

    public static void main(String[] args) throws Exception {
        Client client = new Client();
        client.recursionfile(new File(filename));



    }

    public void recursionfile(File file) throws  Exception {

        File [] fileArray = file.listFiles();

        for (File files :
                fileArray) {
            System.out.println("files== " + files);
            if (files.isDirectory()) { //是否为文件夹
                ClientThread1 clientThread = new ClientThread1(files.getPath(),1);  //1 表示文件夹
                clientThread.start();
                clientThread.join();

                recursionfile(files);

            } else {
                ClientThread1 clientThread1 = new ClientThread1(files.getPath(),2); //2表示文件
                clientThread1.start();
            }
        }




    }
}

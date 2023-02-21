package everyclientonefile.client2;



import java.io.File;

public class Client2 {

//    private static String filename = "D:\\JavaLearn";

    public static void main(String[] args) throws Exception {
        Client2 client = new Client2();
        client.recursionfile(new File("D:\\服务器"));



    }

    public void recursionfile(File file) throws  Exception {

        File [] fileArray = file.listFiles();

        for (File files :
                fileArray) {
            System.out.println("files== " + files);
            if (files.isDirectory()) { //是否为文件夹
                ClientThread2 clientThread2 = new ClientThread2(files.getPath(),1);  //1 表示文件夹
                clientThread2.start();
                clientThread2.join();

                recursionfile(files);

            } else {
                ClientThread2 clientThread2 = new ClientThread2(files.getPath(),2); //2表示文件
                clientThread2.start();
            }
        }




    }
}

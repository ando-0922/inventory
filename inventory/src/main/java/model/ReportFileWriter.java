package model;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class ReportFileWriter {

	public static void main(String[] args) {
        String[] names = { "山田", "田村", "村上", "上条" };
        int[] scores = { 55, 33, 44, 11, 99 };
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter("text3.csv", true));
            for (int i = 0; i < names.length; i++) {
                bw.write(names[i] + "," + scores[i]);
                bw.newLine();
            }
            bw.close();
            System.out.println("書き込み完了");
        } catch (IOException e) {
            System.out.println("書き込みエラー");
        }
    }

}

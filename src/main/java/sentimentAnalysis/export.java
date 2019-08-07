/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sentimentAnalysis;
import java.io.FileOutputStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
/*
* @Author : Somasundar Kanakaraj
* Date : 24-06-17
* Process : Exporting Database data to Excel 
*/
public class export{
public static void main(String[] args) {
export samp = new export();
samp.disp();
}
public void disp(){
try{
String filename="data.xls" ;
HSSFWorkbook hwb=new HSSFWorkbook();
HSSFSheet sheet = hwb.createSheet("new sheet");

HSSFRow rowhead= sheet.createRow((short)0);
rowhead.createCell(0).setCellValue("Tweet ID");
rowhead.createCell(1).setCellValue("Tweeter");
rowhead.createCell(2).setCellValue("Tweet Text");
rowhead.createCell(3).setCellValue("Analysis");
rowhead.createCell(4).setCellValue("Very Positive");
rowhead.createCell(5).setCellValue("Positive");
rowhead.createCell(6).setCellValue("Neutral");
rowhead.createCell(7).setCellValue("Negative");
rowhead.createCell(8).setCellValue("Very Negative");


SQLData d=new SQLData();
ResultSet rs=d.tableData();
int i=1;
while(rs.next()){
HSSFRow row= sheet.createRow((short)i);
row.createCell(0).setCellValue(rs.getLong(1));
row.createCell(1).setCellValue(rs.getString(3));
row.createCell(2).setCellValue(rs.getString(2));
row.createCell(3).setCellValue(rs.getString(4));
row.createCell(4).setCellValue(rs.getInt(5)+"%");
row.createCell(5).setCellValue(rs.getInt(6)+"%");
row.createCell(6).setCellValue(rs.getInt(7)+"%");
row.createCell(7).setCellValue(rs.getInt(8)+"%");
row.createCell(8).setCellValue(rs.getInt(9)+"%");
i++;
}
FileOutputStream fileOut = new FileOutputStream(filename);
hwb.write(fileOut);
fileOut.close();
System.out.println("Your excel file has been generated!");
} catch ( Exception ex ){
System.out.println(ex);
}
}
}
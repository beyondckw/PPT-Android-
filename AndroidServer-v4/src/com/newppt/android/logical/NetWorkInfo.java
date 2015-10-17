package com.newppt.android.logical;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

  
public class NetWorkInfo{  
     
	String subnet;  
    String ip;
    Map<String, String> map = new HashMap<String, String>();
	String ipAddress;
	String subNetMask;
	boolean flag = false; 				//�������һ��IP��ַ���������� ��λ�����ڵļ�Ϊһ�ԣ�
	
    public NetWorkInfo(String ip) {
    	this.ip=ip;
    }
    
	 /**
	  * �õ����������е�IP��ַ����������
	  * @return
	  */
	 public Map<String, String> getIPAddressAndMask() {  
		 String os = System.getProperty("os.name");  
		 if (os != null && os.startsWith("Windows")) {  
			 try {    
				 String command = "cmd.exe /c ipconfig /all"; 
				 Process p = Runtime.getRuntime().exec(command);   
				 BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream(), "gb2312"));   
				 String line;     
				 while ((line = br.readLine()) != null) {   
					 /*
					  * ��û���IP��ַ 
					  * ���ԡ�IPv4����ͷ����һ�л�ȡip��ַ��Ӣ�İ����ԡ�IP Address����ͷ
					  * ��ʽΪ��
					  * IPv4 ��ַ . . . . . . . . . . . . : 10.10.116.132(��ѡ)
					  */
					 if (line.indexOf("IPv4 ��ַ") > 0 || line.indexOf("IP Address") > 0) {  
						 int index = line.indexOf(":");  
						 index += 2;    
						 ipAddress = line.substring(index);
						 
						 //ȥ������    ��10.10.116.132(��ѡ)��
						 int i;
						 char [] array = ipAddress.toCharArray();
						 for(i=0; i<array.length; i++){
							 if(array[i]!='.' && (array[i]<'0'||array[i]>'9'))
								 break;
						 }
						 ipAddress = ipAddress.substring(0, i);
						   
						 flag = true; 			 //����IP��ַ���ش�������Ӧ����������
					 }     
					      
					 /*	
					  * ��û����������� 
					  * ���ԡ��������롱��ͷ����һ�л�ȡ�������룬Ӣ�İ����ԡ�Subnet Mask����ͷ
					  * ��ʽΪ��
					  * ��������  . . . . . . . . . . . . : 255.255.255.192
					  */
					 if (flag==true && (line.indexOf("�������� ") > 0 || line.indexOf("Subnet Mask") > 0)) {    
						 int index = line.indexOf(":");      
						 index += 2;      
						 subNetMask = line.substring(index);     
						 flag = false;  //���ĵ�
						 
						 map.put(ipAddress, subNetMask);
					 }      
				 }   
				 br.close();   
			 } catch (IOException e) {  
				e.printStackTrace();	 
			 }   
		 }
		return map;    
	 }     
    
	 /**
	   * ��ȡ���ӦIP��ַ����������
	   */
	 public void setSubNetMask(){
		 subnet = getIPAddressAndMask().get(ip);
		 System.out.println(ip+"----");
		 System.out.println(subnet+"----");
	 }  
		 
    
     /**
      * ���ݱ���IP���������룬���������㲥��ַ 
     * @return String
     */
    public String getBroadcastAddress() {  
    	 setSubNetMask();
         String[] ips = ip.split("\\.");   
         String[] subnets = subnet.split("\\.");  
         StringBuffer sb = new StringBuffer();  
         for(int i = 0; i < ips.length; i++) {  
        	 /***
        	  * ��IP��ַ������������������ַ ips[i] = String.valueOf(Integer.parseInt(subnets[i]) & Integer.parseInt(ips[i])); 
        	  */
        	 /****
        	  * ��IP��ַ�������������㲥��ַ  ips[i] = String.valueOf((~Integer.parseInt(subnets[i]))|(Integer.parseInt(ips[i])));
        	  */
        	
        	 ips[i] = String.valueOf((~Integer.parseInt(subnets[i]))|(Integer.parseInt(ips[i]))); 
             sb.append(turnToStr(Integer.parseInt(ips[i])));  
             if(i != (ips.length-1))  
                 sb.append(".");  
         }      
//         System.out.println(turnToIp(sb.toString()));
         return turnToIp(sb.toString());  
    }  
  
     
     /** 
      * �Ѵ���������ת��Ϊ������ 
      * @param num 
      * @return 
      */  
    private String turnToStr(int num) {  
        String str = "";  
        str = Integer.toBinaryString(num);            
        int len = 8 - str.length();  
        // �����������������8λ,��ǰ�油��.  
        for (int i = 0; i < len; i++) {  
            str = "0" + str;  
        }  
        //���numΪ������תΪ�����ƵĽ����32λ����1111 1111 1111 1111 1111 1111 1101 1110  
        //��ֻȡ����8λ.  
        if (len < 0)  
            str = str.substring(24, 32);  
        return str;  
    }      
      
    /** 
     * �Ѷ�������ʽ��ip��ת��Ϊʮ������ʽ��ip 
     * @param str 
     * @return 
     */  
    private String turnToIp(String str){  
        String[] ips = str.split("\\.");  
        StringBuffer sb = new StringBuffer();  
        for (int i = 0; i < ips.length; i++) {  
            sb.append(turnToInt(ips[i]));  
            sb.append(".");  
        }            
        sb.deleteCharAt(sb.length() - 1);  
        return sb.toString();  
    }  
  
    /** 
     * �Ѷ�����ת��Ϊʮ���� 
     * @param str 
     * @return 
     */  
    private int turnToInt(String str){  
        int total = 0;  
        int top = str.length();  
        for (int i = 0; i < str.length(); i++) {  
            String h = String.valueOf(str.charAt(i));  
            top--;  
            total += ((int) Math.pow(2, top)) * (Integer.parseInt(h));  
        }  
        return total;  
    }
    
    
    
} 
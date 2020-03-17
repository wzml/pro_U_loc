package com.example.login_server.MyUtils;
import java.security.MessageDigest;

//  md5加盐加密 salt = id (ps:虽然也不是很安全，比较安全的salt应当是随机值
public class MD5Encode {
    private static final String hexDigist[] = {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "a", "b","c","d","e","f"};

    //  jmcontent 加密内容  jycontent  加盐内容
    public static String MD5EncodeUtf8(String jmContent,String jyContent){
        jmContent = jmContent + jyContent;
        return MD5Encode0(jmContent,"utf8");
    }
    //  返回大写的MD5
    private static String MD5Encode0(String jmContent, String charsetname) {
        String resultString = null;
        try {
            resultString = new String(jmContent);
            MessageDigest md = MessageDigest.getInstance("MD5");
            if(charsetname == null || "".equals(charsetname)){
                resultString = byteArrayToHexString(md.digest(resultString.getBytes()));
            } else {
                resultString = byteArrayToHexString(md.digest(resultString.getBytes(charsetname)));
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return resultString;
    }
    //  遍历8个byte，转为16进制字符
    private static String byteArrayToHexString(byte[] b) {
        StringBuffer resultSb = new StringBuffer();
        for(int i = 0;i < b.length;i++){
            resultSb.append(byteToHexString(b[i]));
        }
        return resultSb.toString();
    }
    //  对单个byte，256的byte通过16(16进制的原因)拆分为d1和d2
    private static String byteToHexString(byte b) {
        int n = b;
        if(n < 0){
            n+=256;
        }
        int d1 = n/16;
        int d2 = n%16;
        return hexDigist[d1] + hexDigist[d2];
    }

}
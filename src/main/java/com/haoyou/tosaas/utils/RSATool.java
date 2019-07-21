package com.haoyou.tosaas.utils;

import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.RSAPrivateKeySpec;
import java.security.spec.RSAPublicKeySpec;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.Cipher;

public class RSATool {

    //模
    static public String modulus = "118729294304678342031467641765989487089673219433966663971017194472382989424787675643934275762945100395793360499193303019874313330534224223190746277824267254652633133603207009042670843574866478680719747357875969633719235321602388941412295149416915040357289835767614730927127217700257292130875183149987176957483";
    //公钥指数
    static String public_exponent = "65537";
    //私钥指数
    static public String private_exponent = "107705479423863417465779853186316233371305556277952700099255599825626950993827561413906351567185133721877822732167176574111840275369954354290496173294602064735988229754591094212461605100350111141686646163877096491296218959583898485026846586277440275604738813600536088298568021045030139933945191387571588076161";

    //定义appid和private_exponent对应map
    static Map<String, String> publicToPrivateMap = new HashMap<String, String>();
    //定义appid和modulus对应map
    static Map<String, String> modulusMap = new HashMap<String, String>();

    static {
        //预置一组私钥指数和模
        modulusMap.put("227116394538313734", "22711639453831373471153395621793366694567748688589203752641519831442109708946078404053125697817200264212906023902702651309853166001673435932998220304111312295867654029619384178323598419865611302461155657629632750589114741225299678940073681620744136747892021087082519016006720970442025759130462722736315208264795934241744497775827842322765968114451776721314226555800754154853703194547541945952146214536501116318194658945736320959090602168550325740779337404070891971448239634932906067013881586039774008905403374123201248133955990926778205309254983784752038114813125021527537659094612702784671758028822762636446327035027");
        publicToPrivateMap.put("227116394538313734", "18323316973341471854277196699676556305729208602170767035677063636841327933088407304818727865883190362238021165262851518765844886082250961312256403258610884329946539194075031189170727113721627965978472378069017532914977079017142915059851013046297900215268363259813557386702768887668823259964052153775118335013455081101954714488964781262635415373462654446535241941390522544174936426993002156260121802494108095393616121478144757758303865337537258205136605857154530309703801446631412757697017688481489826161065828863675001540000850320643261289924660682474825499166736602367773931440731014505941515959934730873107467668193");
    }

    public static void main(String[] args) throws Exception {
        // TODO Auto-generated method stub
        HashMap<String, Object> map = RSATool.getKeys();
        //生成公钥和私钥
        RSAPublicKey publicKey = (RSAPublicKey) map.get("public");
        RSAPrivateKey privateKey = (RSAPrivateKey) map.get("private");

        //模
        modulus = publicKey.getModulus().toString();
        //公钥指数
        public_exponent = publicKey.getPublicExponent().toString();
        //私钥指数
        private_exponent = privateKey.getPrivateExponent().toString();
        //明文
        String ming = "8a9f8c7c8504442b9bac98f2839336a9";
        System.err.println("ming len=" + ming.length());
        String token = RSATool.MakeToken(ming);
        String content = RSATool.ValidateToken(token);
        System.err.println(content);
        //使用模和指数生成公钥和私钥
        RSAPublicKey pubKey = RSATool.getPublicKey(modulus, public_exponent);
        RSAPrivateKey priKey = RSATool.getPrivateKey(modulus, private_exponent);
        //加密后的密文
        String mi = RSATool.encryptByPublicKey(ming, pubKey);
        System.err.println(mi);
        //解密后的明文
        ming = RSATool.decryptByPrivateKey(mi, priKey);
        System.err.println(ming);

        String tokenJia = MakeTokenByPublicKey("374BBA0EF51041693965CB5ED2C2B967FC91FE9B85DE5C1087F06D2FC3AA61DB28BCE1261042C9DA3D2E880852547A7581D62AEE51AA9C451C28AB32090FB72CB2A14BE6B36F602E93A351828EFE18559B6DF2C074BCA733D4D16C2A5A72BB8F3ACCCD98ABB5FE69EA61D1562E55F5A473B8EC322B8966C228A93BCF337DBF6233DB4F64203F12B0F16030C173BE5C77D784E95D506CA0BACFB9497954FF8EEEFF4CA13A9CB80AA3BF38921A9D2B6B006B05A4B57BDABCD902B710CCDDB2218977E9D481497728A5E22F92BF8F94B2D7BF6A66BACDC71B9141A35084063A6E686B915EB89107AE9B3D3506795AD297344BFB1FF3C6AF1891CE8803093A6B6C5B", "227116394538313734");

        System.out.println(tokenJia);

        String tokenjie = ValidateTokenByPublicKey(tokenJia, "227116394538313734");

        System.out.println(tokenjie);

    }

    /// <summary>
    /// RSA加密
    /// </summary>
    /// <param name="publickey"></param>
    /// <param name="content"></param>
    /// <returns></returns>
    static String RSAEncrypt(String content) {
        //使用模和指数生成公钥和私钥
        RSAPublicKey pubKey = RSATool.getPublicKey(modulus, public_exponent);
        RSAPrivateKey priKey = RSATool.getPrivateKey(modulus, private_exponent);
        //加密后的密文
        String mi = "";
        try {
            mi = RSATool.encryptByPublicKey(content, pubKey);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return mi;
    }

    /**
     * 根据公钥指数加密数据
     *
     * @param content
     * @param publicExponent
     * @return
     */
    static String RSAEncryptByPublicExponent(String content, String appId) {
        //加密后的密文
        String mi = "";
        String key_modulus = modulusMap.get(appId);
        if (key_modulus != null) {
            //使用模和指数生成公钥和私钥
            RSAPublicKey pubKey = RSATool.getPublicKey(key_modulus, public_exponent);
            try {
                mi = RSATool.encryptByPublicKey(content, pubKey);
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return mi;
    }

    /**
     * 根据公钥指数解析数据
     *
     * @param content
     * @param publicExponent
     * @return
     */
    static String RSADecryptByPublicExponent(String content, String appId) {
        //解密后的明文
        String ming = "";
        //使用模和指数生成公钥和私钥
        String key_modulus = modulusMap.get(appId);
        String privateExponent = publicToPrivateMap.get(appId);
        if (privateExponent != null && key_modulus != null) {
            RSAPrivateKey priKey = RSATool.getPrivateKey(key_modulus, privateExponent);
            try {
                ming = RSATool.decryptByPrivateKey(content, priKey);
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return ming;
    }

    /// <summary>
    /// RSA解密
    /// </summary>
    /// <param name="privatekey"></param>
    /// <param name="content"></param>
    /// <returns></returns>
    static String RSADecrypt(String content) {
        //使用模和指数生成公钥和私钥
        RSAPublicKey pubKey = RSATool.getPublicKey(modulus, public_exponent);
        RSAPrivateKey priKey = RSATool.getPrivateKey(modulus, private_exponent);
        //解密后的明文
        String ming = "";
        try {
            ming = RSATool.decryptByPrivateKey(content, priKey);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return ming;
    }

    /**
     * 生成公钥和私钥
     *
     * @throws NoSuchAlgorithmException
     */
    public static HashMap<String, Object> getKeys() throws NoSuchAlgorithmException {
        HashMap<String, Object> map = new HashMap<String, Object>();
        KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance("RSA");
        keyPairGen.initialize(1024);
        KeyPair keyPair = keyPairGen.generateKeyPair();
        RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
        RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();
        map.put("public", publicKey);
        map.put("private", privateKey);
        return map;
    }

    /**
     * 使用模和指数生成RSA公钥
     * 注意：【此代码用了默认补位方式，为RSA/None/PKCS1Padding，不同JDK默认的补位方式可能不同，如Android默认是RSA
     * /None/NoPadding】
     *
     * @param modulus  模
     * @param exponent 指数
     * @return
     */
    public static RSAPublicKey getPublicKey(String modulus, String exponent) {
        try {
            BigInteger b1 = new BigInteger(modulus);
            BigInteger b2 = new BigInteger(exponent);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            RSAPublicKeySpec keySpec = new RSAPublicKeySpec(b1, b2);
            return (RSAPublicKey) keyFactory.generatePublic(keySpec);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 使用模和指数生成RSA私钥
     * 注意：【此代码用了默认补位方式，为RSA/None/PKCS1Padding，不同JDK默认的补位方式可能不同，如Android默认是RSA
     * /None/NoPadding】
     *
     * @param modulus  模
     * @param exponent 指数
     * @return
     */
    public static RSAPrivateKey getPrivateKey(String modulus, String exponent) {
        try {
            BigInteger b1 = new BigInteger(modulus);
            BigInteger b2 = new BigInteger(exponent);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            RSAPrivateKeySpec keySpec = new RSAPrivateKeySpec(b1, b2);
            return (RSAPrivateKey) keyFactory.generatePrivate(keySpec);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 公钥加密
     *
     * @param data
     * @param publicKey
     * @return
     * @throws Exception
     */
    public static String encryptByPublicKey(String data, RSAPublicKey publicKey)
            throws Exception {
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        // 模长
        int key_len = publicKey.getModulus().bitLength() / 8;
        //System.out.println("data length="+data.length());
//		// 加密数据长度 <= 模长-11
//		List<String> datas = splitString(data, key_len - 11);
//		String mi = "";
//		//如果明文长度大于模长-11则要分组加密
//		for (String s : datas) {
//			mi += bcd2Str(cipher.doFinal(s.getBytes()));
//		}
        byte[] bytes = data.getBytes();
        //如果密文长度大于模长则要分组解密
        String mi = "";
        byte[][] arrays = splitArray(bytes, 117);//长度不能超过117
        for (byte[] arr : arrays) {
            mi += bcd2Str(cipher.doFinal(arr));
        }
        return mi;
    }

    /**
     * 私钥解密
     *
     * @param data
     * @param privateKey
     * @return
     * @throws Exception
     */
    public static String decryptByPrivateKey(String data, RSAPrivateKey privateKey)
            throws Exception {
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        //模长
        int key_len = privateKey.getModulus().bitLength() / 8;
        byte[] bytes = data.getBytes();
        byte[] bcd = ASCII_To_BCD(bytes, bytes.length);
        System.err.println(bcd.length);
        //如果密文长度大于模长则要分组解密
        String ming = "";
        byte[][] arrays = splitArray(bcd, key_len);
        for (byte[] arr : arrays) {
            ming += new String(cipher.doFinal(arr));
        }
        return ming;
    }

    /**
     * ASCII码转BCD码
     */
    public static byte[] ASCII_To_BCD(byte[] ascii, int asc_len) {
        byte[] bcd = new byte[asc_len / 2];
        int j = 0;
        for (int i = 0; i < (asc_len + 1) / 2; i++) {
            bcd[i] = asc_to_bcd(ascii[j++]);
            bcd[i] = (byte) (((j >= asc_len) ? 0x00 : asc_to_bcd(ascii[j++])) + (bcd[i] << 4));
        }
        return bcd;
    }

    public static byte asc_to_bcd(byte asc) {
        byte bcd;

        if ((asc >= '0') && (asc <= '9'))
            bcd = (byte) (asc - '0');
        else if ((asc >= 'A') && (asc <= 'F'))
            bcd = (byte) (asc - 'A' + 10);
        else if ((asc >= 'a') && (asc <= 'f'))
            bcd = (byte) (asc - 'a' + 10);
        else
            bcd = (byte) (asc - 48);
        return bcd;
    }

    /**
     * BCD转字符串
     */
    public static String bcd2Str(byte[] bytes) {
        char temp[] = new char[bytes.length * 2], val;

        for (int i = 0; i < bytes.length; i++) {
            val = (char) (((bytes[i] & 0xf0) >> 4) & 0x0f);
            temp[i * 2] = (char) (val > 9 ? val + 'A' - 10 : val + '0');

            val = (char) (bytes[i] & 0x0f);
            temp[i * 2 + 1] = (char) (val > 9 ? val + 'A' - 10 : val + '0');
        }
        return new String(temp);
    }

    /**
     * 拆分字符串
     */
    public static String[] splitString(String string, int len) {
        int x = string.length() / len;
        int y = string.length() % len;
        int z = 0;
        if (y != 0) {
            z = 1;
        }
        String[] strings = new String[x + z];
        String str = "";
        for (int i = 0; i < x + z; i++) {
            if (i == x + z - 1 && y != 0) {
                str = string.substring(i * len, i * len + y);
            } else {
                str = string.substring(i * len, i * len + len);
            }
            strings[i] = str;
        }
        return strings;
    }

    /**
     * 拆分数组
     */
    public static byte[][] splitArray(byte[] data, int len) {
        int x = data.length / len;
        int y = data.length % len;
        int z = 0;
        if (y != 0) {
            z = 1;
        }
        byte[][] arrays = new byte[x + z][];
        byte[] arr;
        for (int i = 0; i < x + z; i++) {
            arr = new byte[len];
            if (i == x + z - 1 && y != 0) {
                System.arraycopy(data, i * len, arr, 0, y);
            } else {
                System.arraycopy(data, i * len, arr, 0, len);
            }
            arrays[i] = arr;
        }
        return arrays;
    }

    //生成登录令牌
    public static String MakeToken(String sContent) {
        if (sContent == "")
            return "";
        Date now = new Date();
        String strContent = String.format("%s,%d", sContent, now.getTime());
        return RSAEncrypt(strContent);
    }

    //验证登录令牌
    public static String ValidateToken(String sToken) {
        if (sToken == null || sToken.length() == 0)
            return "";

        String sContent = RSADecrypt(sToken);
        sContent = sContent.trim();
        int idx = sContent.lastIndexOf(',');
        if (idx < 0)
            return "";
        String sTime = sContent.substring(idx + 1);
        String sJson = sContent.substring(0, idx);

        long lTime = Long.parseLong(sTime);
        //令牌只能用一天
        Date now = new Date();
        long dis = now.getTime() - lTime;
        if (dis > 24 * 60 * 60 * 1000)
            return "";
        return sJson;
    }


    /**
     * 根据公钥指数生成登陆令牌
     *
     * @param sJson
     * @param appId
     * @return
     */
    public static String MakeTokenByPublicKey(String sJson, String appId) {
        if (sJson == "")
            return "";
        Date now = new Date();
        String strContent = String.format("%s,%d", sJson, now.getTime());
        return RSAEncryptByPublicExponent(strContent, appId);
    }

    /**
     * 根据公钥指数解析登陆令牌
     *
     * @param sToken
     * @param appId
     * @return
     */
    public static String ValidateTokenByPublicKey(String sToken, String appId) {
        if (sToken == null || sToken.length() == 0)
            return "";

        String sContent = RSADecryptByPublicExponent(sToken, appId);
        sContent = sContent.trim();
        int idx = sContent.lastIndexOf(',');
        if (idx < 0)
            return "";
        String sTime = sContent.substring(idx + 1);
        String sJson = sContent.substring(0, idx);

        long lTime = Long.parseLong(sTime);
        //令牌只能用一天
        Date now = new Date();
        long dis = now.getTime() - lTime;
        if (dis > 24 * 60 * 60 * 1000)
            return "";
        return sJson;
    }
}


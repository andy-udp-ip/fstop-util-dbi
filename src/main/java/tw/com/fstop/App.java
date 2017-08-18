package tw.com.fstop;

import java.nio.charset.StandardCharsets;

import tw.com.fstop.util.Base64;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {
        System.out.println( "Hello World!" );
        Base64.Decoder decoder;
        Base64.Encoder encoder = Base64.getEncoder();
        final String text = "0123456789abcdefghijklmnopqrstuvwxyz ";
        final byte[] encodeByte = text.getBytes(StandardCharsets.UTF_8);
        final String encodedText = encoder.encodeToString(encodeByte);
        System.out.println(encodedText);
        
    }
}

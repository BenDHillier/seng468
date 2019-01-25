
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;

import java.io.File;
import java.util.Scanner;

public class WorkloadGenerator {
    public static void main(String args[]) {
        try {
            if( args.length < 1 ) {
                System.out.println("Got here");
                throw new ArrayIndexOutOfBoundsException("Program requires at least one argument, a workload generation filename.");
            }
            else {
                File f = new File(args[0]);
                Scanner scanner = new Scanner(f);
                HttpClient httpClient = HttpClientBuilder.create().build();
                HttpPut httpPut = new HttpPut("localhost:8080");
                while( scanner.hasNextLine() ) {
                    String line = scanner.nextLine();
                    String[] split = line.split(" ");
                    System.out.println(split[1]);
                }
            }
        }
        catch( Exception e ) {
            System.out.println("Exception: " + e.getMessage());
        }
    }
}
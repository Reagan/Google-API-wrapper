package org.aprilsecond.googleoauthwrapper;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson.JacksonFactory;
import com.google.api.services.calendar.CalendarScopes;
import java.awt.Desktop;
import java.awt.Desktop.Action;
import java.io.IOException;
import java.net.URI;
import java.util.Arrays;
import java.util.List;
import javax.swing.JOptionPane;
import org.aprilsecond.asremind.configurations.Configurations;
import org.aprilsecond.authenticationserver.AuthenticationServer;

/**
 * <p>
 * This class creates a wrapper class that gets data for a specific user's
 * Google calendar. This class implements the recommended OAuth flow for 
 * accessing the Google API where the user must grant access to the
 * application using a web browser and paste the code onto the application.
 * However the implementation uses a minimal server application that captures the
 * returned code and stores the authorization code
 * </p>
 *<p> The client ID and secret are not secured. This is difficult for installed 
 * applications
 * </p>
 * <p>
 * In this code, it attempts to open the browser using {@link 
 * Desktop#isDesktopSupported()}. If that fails on windows, it opens 
 * the browser specified in {@link #BROWSER} using the default value
 * i.e. Google Chrome
 * </p>
 * <p>
 * This code has been adapted from the Google Calendar API example
 * at http://code.google.com/p/google-api-java-client/source/browse/shared/shared-sample-cmdline/src/main/java/com/google/api/services/samples/shared/cmdline/oauth2/OAuth2Native.java?repo=samples
 * @author Yaniv Inbar
 * </p>
 * 
 * @author Reagan Mbitiru <reaganmbitiru@gmail.com>
 */
public class GoogleOAuthAPiWrapper {

    /**
     * stores  a configuration object
     */
    private Configurations configs ;
   
    /**
     * stores the default browser for use in the application
     */
    private static final String BROWSER 
            = "google-chrome" ;
    
    /**
     * stores the port with which to access the 
     * authorization code. Using port in Dynamic range
     * {@link http://www.speedguide.net/ports.php?filter=9091}
     */
    private static int port = 65500 ;
    
    /**
     *** DEFAULT SETTINGS FOR APPLICATION ***
     */
    /**
     * client ID for the application
     */
    private static String clientId = "161984073788.apps.googleusercontent.com";
    
    /**
     * client secret for the application
     */
    private static String clientSecret = "GxuW8kypMYIc5pYUKTuLHM5W";
    
    /**
     * redirect URI for the application
     */
    private static String redirectURI = "http://localhost:" + port ;
    
      /**
     * API key for the application
     */
    private String APIKey = "AIzaSyDsnT8j9G9kcH9XcxGdvn6zJ-_bsnOSAZ8";
       
    /**
     * stores the authorization code for the application
     */
    public String authCode ;
    
    /**
     * stores scopes for the application
     */
    private List<String> SCOPES = Arrays.asList(CalendarScopes.CALENDAR) ;
    
    /**
     * stores the HTTP transport object
     */
    public HttpTransport httpTransport ;
        
    /**
     * stores the JSON object
     */
    public JacksonFactory jsonFactory ;
    
    /**
     * stores the credential for the object
     */
    public Credential credential ;
    
    /**
     * stores the authentication server
     */
    private AuthenticationServer server ;
    
    /**
     * constructor initializes the transport and 
     * json objects
     */
    public GoogleOAuthAPiWrapper(Configurations configurations) {
        
        // initialize the HttpTransport and json factory objs
        httpTransport = new NetHttpTransport();
        jsonFactory = new JacksonFactory();
        
        // loads the application settings
        loadCalendarSettings(configurations) ;
    }
    
    /**
     * method authenticates with Google OAuth
     */
    public boolean authenticate() 
        throws IOException {
      
        // stores state on whether the 
        // app has been authorized
        boolean authenticated = false;

        // create google authorization flow
        GoogleAuthorizationCodeFlow flow 
                = createGoogleAuthorizationCodeFlow(httpTransport, jsonFactory);

        // check if the application has the
        // authorization code stored in the config file before 
        // trying to authenticate
        if ("".equals(authCode)) {
            // open the browser and allow the user to authenticate
            browse(flow.newAuthorizationUrl().setRedirectUri(redirectURI).build());
            
            // start the server
            startServer(port);

            // wait for the user to enter the code
            authCode = getAuthCode();

            // get the access token for the Authorization code flow
            GoogleTokenResponse response 
                = flow.newTokenRequest(authCode)
                    .setRedirectUri(redirectURI).execute();
            
            // write the acces URL, refresh & access tokens to file
            configs.writePropertyToConfigFile("ACCESS_TOKEN", response.getAccessToken());
            configs.writePropertyToConfigFile("REFRESH_TOKEN", response.getRefreshToken());
            configs.writePropertyToConfigFile("AUTH_CODE", authCode) ; 
        
            // create the credential object
            credential = flow.createAndStoreCredential(response, null);
        
        } else  {
            // set up a credential object 
            credential = new GoogleCredential.Builder()
                .setJsonFactory(jsonFactory).setTransport(httpTransport)
                .setClientSecrets(clientId, clientSecret).build() ;

            // add the access and refresh tokens
            credential.setAccessToken(configs.getValueOf("ACCESS_TOKEN")) ;
            credential.setRefreshToken(configs.getValueOf("REFRESH_TOKEN")) ;
        }

        // set the authentication status
        if (credential != null) {
            authenticated = false;
        } else {
            authenticated = true;
        }            

        return authenticated;
    }        
    
    /**
     * This method loads the client secret and ID from the 
     * application config file
     */
    private void loadCalendarSettings(Configurations configurations) {
        
        // initialize cons=figurations object
        configs = configurations ;       
          
        // set the client ID
        clientId = configs.getValueOf("CLIENT_ID", clientId);

        // set the client secret
        clientSecret = configs.getValueOf("CLIENT_SECRET", clientSecret);

        // set the app port
        port = Integer.parseInt(configs.getValueOf("PORT", String.valueOf(port)));

        // set the redirect URI
        redirectURI = configs.getValueOf("REDIRECT_URI", redirectURI);

        // set the API key
        APIKey = configs.getValueOf("API_KEY", APIKey);

        // get the authorization code. default value is empty string
        authCode = configs.getValueOf("AUTH_CODE", "");

    }
    
    /**
     * starts the minimal server that waits for the 
     * authorization code on a new server thread
     */
    private void startServer(final int port) {
        
        Runnable serverThread = new Runnable() {

            @Override
            public void run() {
                // start the server
                server = AuthenticationServer.getInstance();
                server.startServer(port);
            }
         } ;
        
        // start the server
        serverThread.run() ;        
    }
        
    /**
     * creates the Google authorization code flow
     */
    private GoogleAuthorizationCodeFlow 
            createGoogleAuthorizationCodeFlow(HttpTransport httpTransport,
            JacksonFactory jsonFactory) {
        
        // initialize the flow
        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(httpTransport,
                jsonFactory,
                clientId,
                clientSecret, SCOPES)
                .setAccessType("offline").setApprovalPrompt("force").build();
        return flow;
    }
    
    /**
     * waits for the authorization code to be returned
     */
    private String getAuthCode() {
        return server.authorizationCode ;
    }
    
    /** 
     * This code opens the default  browser and 
     * launches the page that allows the user to authenticate
     * using their Gmail credentials and grant the application
     * access. 
     * 
     * @author Yaniv Inbar 
     * @link http://code.google.com/p/google-api-java-client/source/browse/shared/shared-sample-cmdline/src/main/java/com/google/api/services/samples/shared/cmdline/oauth2/OAuth2Native.java?repo=samples
     * @param url 
     */
    private void browse(String url) {

        // first try the Java Desktop
        if (Desktop.isDesktopSupported()) {
            Desktop desktop = Desktop.getDesktop();
            if (desktop.isSupported(Action.BROWSE)) {
                try {
                    desktop.browse(URI.create(url));
                    return;
                } catch (IOException e) {
                }
            }
        }

        // Next try rundll32 (only works on Windows)
        try {
            Runtime.getRuntime().exec("rundll32 url.dll,FileProtocolHandler " + url);
            return;
        } catch (IOException e) {
            // handled below
        }

        // Next try the requested browser (e.g. "google-chrome")
        if (BROWSER != null) {
            try {
                Runtime.getRuntime().exec(new String[]{BROWSER, url});
                return;
            } catch (IOException e) {
                // handled below
            }
        }
        // Finally just ask user to open in their browser and copy-paste
        JOptionPane.showMessageDialog(null, "Please open the following URL in "
                + "your browser: " + url, "Enter AuthorizationCode", JOptionPane.INFORMATION_MESSAGE);
    }
}
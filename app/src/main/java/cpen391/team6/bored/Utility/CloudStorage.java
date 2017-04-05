package cpen391.team6.bored.Utility;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.apache.ApacheHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.storage.Storage;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by andytertzakian on 2017-03-30.
 */

/**
 * Simple wrapper around the Google Cloud Storage API
 */
public class CloudStorage {

    private static volatile CloudStorage singleton = null;

    private static String bucketName;
    private static Storage storage;


    /**
     * A private constructor used only by the build() function in this class.
     * All parameters are mandatory.
     * <p/>
     * A Credential can be build with the CredentialBuilder class.
     *
     * @param bucketName
     * @param credential
     */
    private CloudStorage(String bucketName, Credential credential)
    {
        if( credential == null )
        {
            throw new IllegalArgumentException("Given Credential was null! Error!");
        }

        if( bucketName == null || bucketName.length() < 1 )
        {
            throw new IllegalArgumentException("Given Bucket name is invalid! Error!");
        }

        this.bucketName = bucketName;
        setupStorage(bucketName, credential);
    }


    /**
     * This function class the private class constructor and passes the given parameters.
     * The constructor also takes care of calling the necessary functions to setup the instance.
     * <p/>
     * The function, which initiates the singleton, is synchronized, so thread-safe.
     * <p/>
     * NOTE: If the bucketName is the same as last initiated singleton bucketName, the singleton
     * will be reused. If not, the singleton will be recreated.
     *
     * @param bucketName The name of the referencing 'Bucket' in the cloud storage.
     * @param credential
     * @return
     */
    public static synchronized CloudStorage build(String bucketName, Credential credential)
    {
        if( bucketName == null || bucketName.length() < 1 )
        {
            throw new IllegalArgumentException("Given Bucket name is invalid! Error!");
        }

        if( singleton != null )
        {
            // If the singleton object is referencing a different bucket, re-create singleton.
            if( !singleton.getBucketName().equals(bucketName) )
            {
                singleton = new CloudStorage(bucketName, credential);
            }
        }
        else
        {
            singleton = new CloudStorage(bucketName, credential);
        }

        return singleton;
    }


    /**
     * Here the actual GoogleStorage instance is built with the given parameters provided.
     * Necessary information, like HttpTransport and JsonFactory, are check in the credential. If
     * not present in credential, stock options are used.
     *
     * @param bucketName
     * @param credential
     * @return
     */
    private static void setupStorage(String bucketName, Credential credential)
    {
        HttpTransport httpTransport;
        JsonFactory jsonFactory;

        if( credential.getTransport() != null )
        {
            httpTransport = credential.getTransport();
        }
        else
        {
            httpTransport = new ApacheHttpTransport();
        }


        if( credential.getJsonFactory() != null )
        {
            jsonFactory = credential.getJsonFactory();
        }
        else
        {
            jsonFactory = new JacksonFactory();
        }

        storage = new Storage
                .Builder(httpTransport, jsonFactory, credential)
                .setApplicationName(bucketName)
                .build();
    }


    public List<String> getCourseNotes(String courseCode){
        List<String> fileNames = new ArrayList<>();



        return fileNames;
    }

    public String getBucketName()
    {
        return bucketName;
    }

    public Storage getStorage()
    {
        return storage;
    }

}
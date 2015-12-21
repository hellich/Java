package fr.ecp.sio.appenginedemo.Upload;

import java.util.UUID;
import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;
import com.google.appengine.api.images.*;
import com.google.appengine.tools.cloudstorage.GcsFileOptions;
import com.google.appengine.tools.cloudstorage.GcsFilename;
import com.google.appengine.tools.cloudstorage.GcsOutputChannel;
import com.google.appengine.tools.cloudstorage.GcsService;
import com.google.appengine.tools.cloudstorage.GcsServiceFactory;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.Map;

/**
 * Created by Helmi-PC on 14/12/2015.
 */
public class Upload {

    public static final String BUCKETNAME = "helmi";

    private static BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();

    //this method resizes an image and generates a new JPEG resized image
    public static byte[] resizeImage(Image oldImage, int height, int width, ImagesService imagesService)
    {
        InputSettings is = new InputSettings();
        is.setOrientationCorrection(InputSettings.OrientationCorrection.CORRECT_ORIENTATION);
        OutputSettings os = new OutputSettings(ImagesService.OutputEncoding.JPEG);
        Transform resizeToCard = ImagesServiceFactory.makeResize(width,height);
        Image squareCardImage = imagesService.applyTransform(resizeToCard, oldImage, is, os);
        byte[] squareCardImageBytes = squareCardImage.getImageData();
        return squareCardImageBytes;
    }

    //this method uploads an image
    public static String UploadFile(HttpServletRequest req, String Key) throws IOException {

        Map<String, List<BlobKey>> blobs = blobstoreService.getUploads(req);
        List<BlobKey> blobKeys = blobs.get(Key);
        BlobKey blobKey = blobKeys.get(0);
        //load high-res image from blobstore into app memory
        Image image = ImagesServiceFactory.makeImageFromBlob(blobKey);
        //transform image
        ImagesService imagesService = ImagesServiceFactory.getImagesService();
        byte[] squareCardImageBytes = resizeImage(image, 100, 100 , imagesService);
        //generate random name
        String uuid = UUID.randomUUID().toString();
        String gcsCardObjectName = uuid + ".jpg";
        String gcsPath = "/gs/" + BUCKETNAME + "/" + gcsCardObjectName;
        GcsFilename gcsCardFilename = new GcsFilename(BUCKETNAME, gcsCardObjectName);
        GcsFileOptions gcsOptions = new GcsFileOptions.Builder().mimeType("image/jpeg").build();
        GcsService gcsService = GcsServiceFactory.createGcsService();
        GcsOutputChannel outputChannel = gcsService.createOrReplace(gcsCardFilename, gcsOptions);
        outputChannel.write(ByteBuffer.wrap(squareCardImageBytes));
        outputChannel.close();

        BlobKey cardBlobKey = blobstoreService.createGsBlobKey(gcsPath);
        //generate the two image URLs
        String photoUrl = imagesService.getServingUrl(ServingUrlOptions.Builder.withBlobKey(blobKey));
        String cardUrl = imagesService.getServingUrl(ServingUrlOptions.Builder.withBlobKey(cardBlobKey));
        return photoUrl;
    }
}
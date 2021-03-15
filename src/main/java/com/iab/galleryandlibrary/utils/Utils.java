package com.iab.galleryandlibrary.utils;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.IntentSender;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import android.util.Size;

import androidx.annotation.RequiresApi;


import com.iab.galleryandlibrary.model.MediaStoreFolderDataModel;
import com.iab.galleryandlibrary.model.MediaStoreItemDataModel;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class Utils {

    // Load Folders for Image
    public static ArrayList<MediaStoreFolderDataModel> loadFoldersListImages(Context context, String[] formatArray, MediaType mediaType) {
        ArrayList<MediaStoreFolderDataModel> foldersNameList = new ArrayList<>();
        ArrayList<String> picPaths = new ArrayList<>();
        String[] projection = new String[]{
                //media-database-columns-to-retrieve
                MediaStore.Images.ImageColumns._ID,
                MediaStore.Images.ImageColumns.DATE_ADDED,
                MediaStore.Images.ImageColumns.SIZE,
                MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME,
                MediaStore.Images.ImageColumns.BUCKET_ID,
                MediaStore.Images.ImageColumns.MIME_TYPE,
                MediaStore.Images.ImageColumns.DATA,
                MediaStore.Images.Thumbnails.DATA

        };
        String selection = null; //sql-where-clause-with-placeholder-variables;
        String[] selectionArgs = null;//new String[] {
        //values-of-placeholder-variables
        //};
        String sortOrder = MediaStore.Images.ImageColumns.DATE_ADDED + " DESC";//sql-order-by-clause;

        Cursor cursor = context.getContentResolver().query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                projection,
                selection,
                selectionArgs,
                sortOrder
        );
        // WE WILL CACHE THE Ids of the Coloumns
        int idColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID);
        int dateAddedColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATE_ADDED);
        int bucketDisplayColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_DISPLAY_NAME);
        int bucketIdColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_ID);
        int mimeTypeColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.MIME_TYPE);
        int dataPathColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        int thumbPathColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Thumbnails.DATA);

        int count = cursor.getCount();
        if (count > 0) {
            cursor.moveToFirst();
            do {
                // Use an ID column from the projection to get
                // a URI representing the media item itself.
                long id = cursor.getLong(idColumn);
                Date dateAdded = new Date(TimeUnit.SECONDS.toMillis(cursor.getLong(dateAddedColumn)));
                String bucketDisplayName = cursor.getString(bucketDisplayColumn);
                int bucketId = cursor.getInt(bucketIdColumn);
                String mimeType = cursor.getString(mimeTypeColumn);
                String dataPath = cursor.getString(dataPathColumn);
                String thumbPath = cursor.getString(thumbPathColumn);

//                String type = mimeType.replace("image/", "");

                // Add this to the Model
                Uri contentURI = ContentUris.withAppendedId(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        id
                );

                if (!mimeType.equals("")) {
                    if (formatArray != null && formatArray.length > 0) {
                        for (String formatType : formatArray) {
                            //TODO all lower case mimeType
                            if (mimeType.endsWith(formatType)) {
                                addValueInModel(context, dataPath, bucketDisplayName, picPaths, bucketId, contentURI, dateAdded, foldersNameList, mediaType, thumbPath);
                            }
                        }
                    } else {
                        addValueInModel(context, dataPath, bucketDisplayName, picPaths, bucketId, contentURI, dateAdded, foldersNameList, mediaType, thumbPath);
                    }

                }
            } while (cursor.moveToNext());
            cursor.close();
        }

        return foldersNameList;
    }

    private static void addValueInModel(Context context, String dataPath, String bucketDisplayName, ArrayList<String> picPaths, int bucketId, Uri contentURI, Date dateAdded, ArrayList<MediaStoreFolderDataModel> foldersNameList, MediaType mediaType, String thumbPath) {

        String folderpaths = dataPath.substring(0, dataPath.lastIndexOf("/"));
        if (bucketDisplayName == null) {
            bucketDisplayName = folderpaths.substring(folderpaths.lastIndexOf("/") + 1);
        }
        folderpaths = folderpaths + "/";
        if (!picPaths.contains(folderpaths)) {
            picPaths.add(folderpaths);
            boolean checkValidUriIs;
//            if (mediaType == MediaPickerPresenterImpl.Type.VIDEO) {
//                checkValidUriIs = checkValidVideoUri(context, contentURI);
//            } else {
//                checkValidUriIs = checkValidImageUri(context, thumbPath);
//            }
//
//            if (!checkValidUriIs) {
//                contentURI = null;
//            }
            // Create MediaStoreFolder Object
            MediaStoreFolderDataModel mediaStoreFolder = new MediaStoreFolderDataModel(bucketId, bucketDisplayName, folderpaths, contentURI, 1, dateAdded);
            foldersNameList.add(mediaStoreFolder);
        } else {
            for (int i = 0; i < foldersNameList.size(); i++) {
                if (foldersNameList.get(i).getFolderPath().equals(folderpaths)) {
                    int numberOfItems = foldersNameList.get(i).getNumberOfMedia() + 1;
                    foldersNameList.get(i).setNumberOfMedia(numberOfItems);
                    // Check if the Date is Greater than the Previous Date
                    if (foldersNameList.get(i).getLastUpdatedDate().before(dateAdded)) {
                        foldersNameList.get(i).setLastUpdatedDate(dateAdded);
                    }
                }
            }

//            if (foldersNameList.get(foldersNameList.size() - 1).getFirstImageURI() == null) {
//                boolean checkValidUriIs;
//                if (mediaType == MediaPickerPresenterImpl.Type.VIDEO) {
//                    checkValidUriIs = checkValidVideoUri(context, contentURI);
//                } else {
//                    checkValidUriIs = checkValidImageUri(context, thumbPath);
//                }
//                if (checkValidUriIs) {
//                    foldersNameList.get(foldersNameList.size() - 1).setUpdatedFirstImageUri(contentURI);
//                }
//            }

        }

    }

    // Load Media Data for Image
    public static ArrayList<MediaStoreItemDataModel> loadMediaImagesList(Context context, int bucketId, String[] formatArray) {
        ArrayList<MediaStoreItemDataModel> imageDataList = new ArrayList<>();

        String[] projection = new String[]{
                //media-database-columns-to-retrieve
                MediaStore.Images.ImageColumns._ID,
                MediaStore.Images.ImageColumns.DATE_ADDED,
                MediaStore.Images.ImageColumns.SIZE,
                MediaStore.Images.ImageColumns.BUCKET_ID,
                MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME,
                MediaStore.Images.ImageColumns.DISPLAY_NAME,
                MediaStore.Images.ImageColumns.MIME_TYPE,
                MediaStore.Images.ImageColumns.DATA

        };
        String selection = MediaStore.Images.Media.BUCKET_ID + " = ? "; //null; //sql-where-clause-with-placeholder-variables;

        String[] selectionArgs = new String[]{"" + bucketId + ""};//new String[] {

        //values-of-placeholder-variables
        //};
        String sortOrder = MediaStore.Images.ImageColumns.DATE_ADDED + " DESC";//sql-order-by-clause;

        Cursor cursor = context.getContentResolver().query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                projection,
                selection,
                selectionArgs,
                sortOrder
        );
        // WE WILL CACHE THE Ids of the Coloumns
        int idColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID);
        int dateAddedColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATE_ADDED);
        int displayNameColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME);
        int bucketNameColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_DISPLAY_NAME);
        int mimeTypeColumn = cursor.getColumnIndex(MediaStore.Images.Media.MIME_TYPE);
        int dataPathColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);

        int count = cursor.getCount();
        if (count > 0) {
            while (cursor.moveToNext()) {
                // Use an ID column from the projection to get
                // a URI representing the media item itself.
                long id = cursor.getLong(idColumn);
                Date dateAdded = new Date(TimeUnit.SECONDS.toMillis(cursor.getLong(dateAddedColumn)));
                String displayName = cursor.getString(displayNameColumn);
                String bucketName = cursor.getString(bucketNameColumn);
                String mimeType = cursor.getString(mimeTypeColumn);
                String dataPath = cursor.getString(dataPathColumn);
//                String type = mimeType.replace("image/", "");
                // Add this to the Model
                Uri contentURI = ContentUris.withAppendedId(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        id
                );

                if (!mimeType.equals("")) {
                    if (formatArray != null && formatArray.length > 0) {
                        for (String formatType : formatArray) {
                            if (mimeType.endsWith(formatType)) {
                                MediaStoreItemDataModel mediaStoreItemDataModel = new MediaStoreItemDataModel(id, contentURI, displayName, bucketName, dateAdded, 0);
                                imageDataList.add(mediaStoreItemDataModel);
                            }
                        }
                    } else {
                        MediaStoreItemDataModel mediaStoreItemDataModel = new MediaStoreItemDataModel(id, contentURI, displayName, bucketName, dateAdded, 0);
                        imageDataList.add(mediaStoreItemDataModel);
                    }

                }
            }
        }

        return imageDataList;
    }

    // Load Folders for Video
    public static ArrayList<MediaStoreFolderDataModel> loadFoldersListVideo(Context context, String[] formatArray, MediaType mediaType) {
        ArrayList<MediaStoreFolderDataModel> foldersNameList = new ArrayList<>();
        ArrayList<String> picPaths = new ArrayList<>();
        String[] projection = new String[]{
                //media-database-columns-to-retrieve
                MediaStore.Video.VideoColumns._ID,
                MediaStore.Video.VideoColumns.DATE_ADDED,
                MediaStore.Video.VideoColumns.SIZE,
                MediaStore.Video.VideoColumns.BUCKET_DISPLAY_NAME,
                MediaStore.Video.VideoColumns.BUCKET_ID,
                MediaStore.Video.VideoColumns.MIME_TYPE,
                MediaStore.Video.VideoColumns.DATA

        };
        String selection = null; //sql-where-clause-with-placeholder-variables;
        String[] selectionArgs = null;//new String[] {

//        String selection = MediaStore.Video.Media.MIME_TYPE + " LIKE " + "\'%" +" ?\'";
//        String[] selectionArgs = new String[]{"mp4"};
//        String[] selectionArgs = null;
        //values-of-placeholder-variables
        //};
        String sortOrder = MediaStore.Video.VideoColumns.DATE_ADDED + " DESC";//sql-order-by-clause;

        Cursor cursor = context.getContentResolver().query(
                MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                projection,
                selection,
                selectionArgs,
                sortOrder
        );
        // WE WILL CACHE THE Ids of the Coloumns
        int idColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media._ID);
        int dateAddedColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATE_ADDED);
        int bucketDisplayNameColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.BUCKET_DISPLAY_NAME);
        int bucketIdColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.BUCKET_ID);
        int mimeTypeColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.MIME_TYPE);
        int dataPathColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA);

        int count = cursor.getCount();
        if (count > 0) {
            cursor.moveToFirst();
            do {
                // Use an ID column from the projection to get
                // a URI representing the media item itself.
                long id = cursor.getLong(idColumn);
                Date dateAdded = new Date(TimeUnit.SECONDS.toMillis(cursor.getLong(dateAddedColumn)));
                String bucketDisplayName = cursor.getString(bucketDisplayNameColumn);
                int bucketId = cursor.getInt(bucketIdColumn);
                String mimeType = cursor.getString(mimeTypeColumn);
                String dataPath = cursor.getString(dataPathColumn);

//                String type = mimeType.replace("video/", "");

                // Add this to the Model
                Uri contentURI = ContentUris.withAppendedId(
                        MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                        id
                );

                if (!mimeType.equals("")) {
                    if (formatArray != null && formatArray.length > 0) {
                        for (String formatType : formatArray) {
                            if (mimeType.endsWith(formatType)) {
                                addValueInModel(context, dataPath, bucketDisplayName, picPaths, bucketId, contentURI, dateAdded, foldersNameList, mediaType, null);
                            }
                        }
                    } else {
                        addValueInModel(context, dataPath, bucketDisplayName, picPaths, bucketId, contentURI, dateAdded, foldersNameList, mediaType, null);
                    }
                }
            } while (cursor.moveToNext());
            cursor.close();
        }
        return foldersNameList;
    }

    // Load Media Data for Video
    public static ArrayList<MediaStoreItemDataModel> loadMediaVideoList(Context context, int bucketId, String[] formatArray) {
        ArrayList<MediaStoreItemDataModel> videoDataList = new ArrayList<>();
        ArrayList<String> picPaths = new ArrayList<>();

        String[] projection = new String[]{
                //media-database-columns-to-retrieve
                MediaStore.Video.VideoColumns._ID,
                MediaStore.Video.VideoColumns.DATE_ADDED,
                MediaStore.Video.VideoColumns.SIZE,
                MediaStore.Video.VideoColumns.BUCKET_ID,
                MediaStore.Video.VideoColumns.BUCKET_DISPLAY_NAME,
                MediaStore.Video.VideoColumns.DISPLAY_NAME,
                MediaStore.Video.VideoColumns.MIME_TYPE,
                MediaStore.Video.VideoColumns.DATA,
                MediaStore.Video.VideoColumns.DURATION

        };
        String selection = MediaStore.Video.Media.BUCKET_ID + " = ? "; //null; //sql-where-clause-with-placeholder-variables;

        String[] selectionArgs = new String[]{"" + bucketId + ""};//new String[] {

        //values-of-placeholder-variables
        //};
        String sortOrder = MediaStore.Video.VideoColumns.DATE_ADDED + " DESC";//sql-order-by-clause;

        Cursor cursor = context.getContentResolver().query(
                MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                projection,
                selection,
                selectionArgs,
                sortOrder
        );
        // WE WILL CACHE THE Ids of the Coloumns
        int idColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media._ID);
        int dateAddedColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATE_ADDED);
//        int displayNameColumn =
//                cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DISPLAY_NAME);
        int displayNameColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DISPLAY_NAME);
        int displayFolderNameColumn = cursor.getColumnIndex(MediaStore.Video.Media.BUCKET_DISPLAY_NAME);
        int mimeTypeColumn = cursor.getColumnIndex(MediaStore.Video.Media.MIME_TYPE);
        int dataPathColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA);
        int durationColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DURATION);

        int count = cursor.getCount();
        if (count > 0) {
            while (cursor.moveToNext()) {
                // Use an ID column from the projection to get
                // a URI representing the media item itself.
                long id = cursor.getLong(idColumn);
                Date dateAdded = new Date(TimeUnit.SECONDS.toMillis(cursor.getLong(dateAddedColumn)));
                String displayName = cursor.getString(displayNameColumn);
                String bucketName = cursor.getString(displayFolderNameColumn);
                String mimeType = cursor.getString(mimeTypeColumn);
                String dataPath = cursor.getString(dataPathColumn);
//                String type = mimeType.replace("video/", "");
                int duration = cursor.getInt(durationColumn);

                // Add this to the Model
                Uri contentURI = ContentUris.withAppendedId(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, id);

                if (!mimeType.equals("")) {
                    if (formatArray != null && formatArray.length > 0) {
                        for (String formatType : formatArray) {
                            if (mimeType.endsWith(formatType)) {
                                MediaStoreItemDataModel mediaStoreItemDataModel = new MediaStoreItemDataModel(id, contentURI, displayName, bucketName, dateAdded, duration);
                                videoDataList.add(mediaStoreItemDataModel);
                            }
                        }
                    } else {
                        MediaStoreItemDataModel mediaStoreItemDataModel = new MediaStoreItemDataModel(id, contentURI, displayName, bucketName, dateAdded, duration);
                        videoDataList.add(mediaStoreItemDataModel);
                    }
                }
            }
        }

        return videoDataList;
    }

    private static boolean checkValidVideoUri(Context context, Uri uri) {
        Bitmap thumbnailBitmap = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
            try {
                thumbnailBitmap = context.getApplicationContext().getContentResolver().loadThumbnail(uri, new Size(100, 100), null);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            ContentResolver contentResolver = context.getContentResolver();
            Long videoId = Long.valueOf(uri.getLastPathSegment());
            thumbnailBitmap = MediaStore.Video.Thumbnails.getThumbnail(contentResolver, videoId, MediaStore.Images.Thumbnails.MINI_KIND, null);

        }
        if (thumbnailBitmap != null) {
            thumbnailBitmap.recycle();
            thumbnailBitmap = null;
            return true;
        }
        return false;
    }

    private static boolean checkValidImageUri(Context context, String thumbPath) {
        Log.e("thumbPath ", "" + thumbPath);
        Bitmap thumbnailBitmap = null;
//        thumbnailBitmap = checkIsImageValid(context, Uri.parse(thumbPath), 100, 100);
        thumbnailBitmap = BitmapFactory.decodeFile(thumbPath);
        if (thumbnailBitmap != null) {
            Log.e("boolean ", "true");
            thumbnailBitmap.recycle();
            thumbnailBitmap = null;
            return true;
        }
        return false;
    }

    @RequiresApi(api = 30)
    public static void createWriteMediaRequest(Activity context, ArrayList<Uri> urisToModify, int EDIT_REQUEST_CODE) {

        ContentResolver contentResolver = context.getApplicationContext().getContentResolver();
        PendingIntent editPendingIntent = null;

        editPendingIntent = MediaStore.createWriteRequest(contentResolver,
                urisToModify);

        // Launch a system prompt requesting user permission for the operation.
        try {
            context.startIntentSenderForResult(editPendingIntent.getIntentSender(),
                    EDIT_REQUEST_CODE, null, 0, 0, 0);
        } catch (IntentSender.SendIntentException e) {
            e.printStackTrace();
        }
    }

    //   // Load Folders for Audio
//    public static ArrayList<MediaStoreFolderDataModel> loadFoldersListAudio(Context context) {
//        ArrayList<MediaStoreFolderDataModel> foldersNameList = new ArrayList<>();
//        ArrayList<String> picPaths = new ArrayList<>();
//        String[] projection = new String[]{
//                //media-database-columns-to-retrieve
//                MediaStore.Audio.AudioColumns._ID,
//                MediaStore.Audio.AudioColumns.DATE_ADDED,
//                MediaStore.Audio.AudioColumns.SIZE,
//                MediaStore.Audio.AudioColumns.DISPLAY_NAME,
//                MediaStore.Audio.AudioColumns.ALBUM_ID,
//                MediaStore.Audio.AudioColumns.MIME_TYPE,
//                MediaStore.Audio.AudioColumns.DATA
//
//        };
//        String selection = null; //sql-where-clause-with-placeholder-variables;
//        String[] selectionArgs = null;//new String[] {
//        //values-of-placeholder-variables
//        //};
//        String sortOrder = MediaStore.Audio.AudioColumns.DATE_ADDED + " DESC";//sql-order-by-clause;
//
//        Cursor cursor = context.getContentResolver().query(
//                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
//                projection,
//                selection,
//                selectionArgs,
//                sortOrder
//        );
//        // WE WILL CACHE THE Ids of the Coloumns
////        val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)
//        int idColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID);
////        val dateModifiedColumn =
////                cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATE_ADDED)
//        int dateAddedColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATE_ADDED);
////        int displayNameColumn =
////                cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DISPLAY_NAME);
////         int displayNameColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DISPLAY_NAME);
//        int displayFolderNameColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DISPLAY_NAME);
//        int displayFolderIdColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID);
//        int mimeTypeColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.MIME_TYPE);
//        int dataPathColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA);
//
//        int count = 0;
//        if (cursor != null) {
//            count = cursor.getCount();
//            if (count > 0) {
//                cursor.moveToFirst();
//                do {
//                    // Use an ID column from the projection to get
//                    // a URI representing the media item itself.
//                    long id = cursor.getLong(idColumn);
//                    Date dateAdded = new Date(TimeUnit.SECONDS.toMillis(cursor.getLong(dateAddedColumn)));
//                    String displayName = cursor.getString(displayFolderNameColumn);
//                    int bucketId = cursor.getInt(displayFolderIdColumn);
//                    String mimeType = cursor.getString(mimeTypeColumn);
//                    String datapath = cursor.getString(dataPathColumn);
//
//                    String type = mimeType.replace("audio/", "");
//
//                    // Add this to the Model
//                    Uri contentURI = ContentUris.withAppendedId(
//                            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
//                            id
//                    );
//
//
//                    if (type != null) {
//                        //  if (type.equals("png") || type.equals("PNG") || type.equals("jpg") || type.equals("jpeg") || type.equals("JPG") || type.equals("JPEG")) {
////                        String folderpaths = datapath.substring(0, datapath.lastIndexOf(displayName + "/"));
//                        String folderpaths = datapath;
//                        folderpaths = folderpaths + displayName + "/";
//                        if (!picPaths.contains(folderpaths)) {
//                            picPaths.add(folderpaths);
//                            // Create MediaStoreFolder Object
//                            MediaStoreFolderDataModel mediaStoreFolder = new MediaStoreFolderDataModel(bucketId, displayName, folderpaths, contentURI, 1, dateAdded);
//                            foldersNameList.add(mediaStoreFolder);
//
//                        } else {
//                            for (int i = 0; i < foldersNameList.size(); i++) {
//                                if (foldersNameList.get(i).getFolderPath().equals(folderpaths)) {
//                                    int numberOfItems = foldersNameList.get(i).getNumberOfMedia() + 1;
//                                    foldersNameList.get(i).setNumberOfMedia(numberOfItems);
//                                    // Check if the Date is Greater than the Previous Date
//                                    if (foldersNameList.get(i).getLastUpdatedDate().before(dateAdded)) {
//                                        foldersNameList.get(i).setLastUpdatedDate(dateAdded);
//                                    }
//                                }
//                            }
//                        }
//                        //}
//                    }
//                } while (cursor.moveToNext());
//                cursor.close();
//            }
//        }
//
//        return foldersNameList;
//    }
//
//
//    // Load Media Data for Audio
//    public static ArrayList<MediaStoreItemDataModel> loadMediaAudioList(Context context, int bucketId) {
//        ArrayList<MediaStoreItemDataModel> AudioDataList = new ArrayList<>();
//        ArrayList<String> picPaths = new ArrayList<>();
//
//        String[] projection = new String[]{
//                //media-database-columns-to-retrieve
//                MediaStore.Audio.AudioColumns._ID,
//                MediaStore.Audio.AudioColumns.DATE_ADDED,
//                MediaStore.Audio.AudioColumns.SIZE,
//                MediaStore.Audio.AudioColumns.ALBUM_ID,
//                MediaStore.Audio.AudioColumns.DISPLAY_NAME,
//                MediaStore.Audio.AudioColumns.MIME_TYPE,
//                MediaStore.Audio.AudioColumns.DATA
//
//        };
//        String selection = MediaStore.Audio.Media.ALBUM_ID + " = ? " ; //null; //sql-where-clause-with-placeholder-variables;
//
//        String[] selectionArgs = new String[] {""+bucketId+""} ;//new String[] {
//
//        //values-of-placeholder-variables
//        //};
//        String sortOrder = MediaStore.Audio.AudioColumns.DATE_ADDED + " DESC";//sql-order-by-clause;
//
//        Cursor cursor = context.getContentResolver().query(
//                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
//                projection,
//                selection,
//                selectionArgs,
//                sortOrder
//        );
//        // WE WILL CACHE THE Ids of the Coloumns
////        val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)
//        int idColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID);
////        val dateModifiedColumn =
////                cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATE_ADDED)
//        int dateAddedColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATE_ADDED);
////        int displayNameColumn =
////                cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DISPLAY_NAME);
////         int displayNameColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DISPLAY_NAME);
//        int displayFolderNameColumn = cursor.getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME);
//        int mimeTypeColumn = cursor.getColumnIndex(MediaStore.Audio.Media.MIME_TYPE);
//        int dataPathColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA);
//
//        int count = 0;
//        if (cursor != null) {
//            count = cursor.getCount();
//            if (count > 0) {
//                while (cursor.moveToNext()) {
//                    // Use an ID column from the projection to get
//                    // a URI representing the media item itself.
//                    long id = cursor.getLong(idColumn);
//                    Date dateAdded = new Date(TimeUnit.SECONDS.toMillis(cursor.getLong(dateAddedColumn)));
//                    String displayName = cursor.getString(displayFolderNameColumn);
//                    String mimeType = cursor.getString(mimeTypeColumn);
//                    String dataPath = cursor.getString(dataPathColumn);
//                    String type = mimeType.replace("audio/", "");
//                    // Add this to the Model
//                    Uri contentURI = ContentUris.withAppendedId(
//                            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
//                            id
//                    );
//
//                    MediaStoreItemDataModel mediaStoreItemDataModel = new MediaStoreItemDataModel(id, contentURI, displayName, dateAdded);
//                    AudioDataList.add(mediaStoreItemDataModel);
//
////                    if (type != null) {
////                        if (type.equals("png") || type.equals("PNG") || type.equals("jpg") || type.equals("jpeg") || type.equals("JPG") || type.equals("JPEG")) {
////                            String folderpaths = datapath.substring(0, datapath.lastIndexOf(displayName + "/"));
////                            folderpaths = folderpaths + displayName + "/";
////                            if (!picPaths.contains(folderpaths)) {
////                                picPaths.add(folderpaths);
////                                // Create MediaStore Audio Object
////                                MediaStoreItemDataModel mediaStoreItemDataModel = new MediaStoreItemDataModel(id, contentURI, displayName, dateAdded);
////                                AudioDataList.add(mediaStoreItemDataModel);
////
////                            } else {
////                                for (int i = 0; i < AudioDataList.size(); i++) {
////                                    if (AudioDataList.get(i).getFolderPath().equals(folderpaths)) {
////                                        int numberOfItems = AudioDataList.get(i).getNumberOfMedia() + 1;
////                                        AudioDataList.get(i).setNumberOfMedia(numberOfItems);
////                                        // Check if the Date is Greater than the Previous Date
////                                        if (AudioDataList.get(i).getLastUpdatedDate().before(dateAdded)) {
////                                            AudioDataList.get(i).setLastUpdatedDate(dateAdded);
////                                        }
////                                    }
////                                }
////                            }
////                        }
////                    }
//                }
//            }
//        }
//
//        return AudioDataList;
//    }
}

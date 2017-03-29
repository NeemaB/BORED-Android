//package cpen391.team6.bored.Utility;
//
///**
// * Created by andytertzakian on 2017-03-20.
// */
//
//import java.io.IOException;
//import java.io.InputStream;
//import com.bumptech.glide.Priority;
//import com.bumptech.glide.load.data.DataFetcher;
//import com.bumptech.glide.load.model.stream.StreamModelLoader;
//import com.google.android.gms.tasks.Tasks;
//import com.google.firebase.storage.StorageReference;
//import com.google.firebase.storage.StreamDownloadTask;
//import android.util.Log;
//
//public class FirebaseImageLoader implements StreamModelLoader<StorageReference> {
//
//    private static final String TAG = "FirebaseImageLoader";
//
//    @Override
//    public DataFetcher<InputStream> getResourceFetcher(StorageReference model, int width, int height) {
//        return new FirebaseStorageFetcher(model);
//    }
//
//    @SuppressWarnings("VisibleForTests")
//    private class FirebaseStorageFetcher implements DataFetcher<InputStream> {
//
//        private StorageReference mRef;
//        private StreamDownloadTask mStreamTask;
//        private InputStream mInputStream;
//
//        FirebaseStorageFetcher(StorageReference ref) {
//            mRef = ref;
//        }
//
//        @Override
//        public String getId() {
//            return mRef.getPath();
//        }
//
//        @Override
//        public InputStream loadData(Priority priority) throws Exception {
//            mStreamTask = mRef.getStream();
//            mInputStream = Tasks.await(mStreamTask).getStream();
//
//            return mInputStream;
//        }
//
//        @Override
//        public void cleanup() {
//            // Close stream if possible
//            if (mInputStream != null) {
//                try {
//                    mInputStream.close();
//                    mInputStream = null;
//                } catch (IOException e) {
//                    Log.w(TAG, "Could not close stream", e);
//                }
//            }
//        }
//
//        @Override
//        public void cancel() {
//            // Cancel task if possible
//            if (mStreamTask != null && mStreamTask.isInProgress()) {
//                mStreamTask.cancel();
//            }
//        }
//    }
//}
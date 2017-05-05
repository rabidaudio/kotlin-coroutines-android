package audio.rabid.talks.kotlinasync;

import android.os.AsyncTask;

import com.example.retrofit.SimpleService;

import java.util.List;

/**
 * Created by cjk on 5/5/17.
 */

public class MainTask extends AsyncTask<String,Void,List<SimpleService.Contributor>> {


    @Override
    protected List<SimpleService.Contributor> doInBackground(String... params) {
        
    }
}

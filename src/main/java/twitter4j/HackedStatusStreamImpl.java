package twitter4j;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;
import twitter4j.conf.Configuration;

import java.io.IOException;


public class HackedStatusStreamImpl extends StatusStreamBase  {

    HackedStatusStreamImpl(String streamId, Dispatcher dispatcher, HttpResponse response, Configuration conf) throws IOException {
        super(dispatcher, response, conf);
    }

    String line;

    static final RawStreamListener[] EMPTY = new RawStreamListener[0];

    @Override
    protected void onClose(){}

    @Override
    public void next(StatusListener listener) throws TwitterException {
        handleNextElement(new StatusListener[]{listener}, EMPTY);
    }

    @Override
    public void next(StreamListener[] listeners, RawStreamListener[] rawStreamListeners) throws TwitterException {
        try {
            handleNextElement(listeners, rawStreamListeners);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected String parseLine(String line) {
        this.line = line;
        return line;
    }

    @Override
    protected void onMessage(String rawString, RawStreamListener[] listeners) throws TwitterException {
        for (RawStreamListener listener : listeners) {
            listener.onMessage(rawString);
        }
    }

    @Override
    protected void onStatus(JSONObject json, StreamListener[] listeners) throws TwitterException {
        for (StreamListener listener : listeners) {
            ((StatusListener) listener).onStatus(asStatus(json));
        }
    }

    @Override
    protected void onDelete(JSONObject json, StreamListener[] listeners) throws TwitterException, JSONException {
        for (StreamListener listener : listeners) {
            JSONObject deletionNotice = json.getJSONObject("delete");
            if (deletionNotice.has("status")) {
                ((StatusListener) listener).onDeletionNotice(new StatusDeletionNoticeImpl(deletionNotice.getJSONObject("status")));
            } else {
                JSONObject directMessage = deletionNotice.getJSONObject("direct_message");
                ((UserStreamListener) listener).onDeletionNotice(ParseUtil.getLong("id", directMessage)
                        , ParseUtil.getLong("user_id", directMessage));
            }
        }
    }

    @Override
    protected void onLimit(JSONObject json, StreamListener[] listeners) throws TwitterException, JSONException {
        for (StreamListener listener : listeners) {
            ((StatusListener) listener).onTrackLimitationNotice(ParseUtil.getInt("track", json.getJSONObject("limit")));
        }
    }

    @Override
    protected void onStallWarning(JSONObject json, StreamListener[] listeners) throws TwitterException, JSONException {
        for (StreamListener listener : listeners) {
            ((StatusListener) listener).onStallWarning(new StallWarning(json));
        }
    }

    @Override
    protected void onScrubGeo(JSONObject json, StreamListener[] listeners) throws TwitterException, JSONException {
        JSONObject scrubGeo = json.getJSONObject("scrub_geo");
        for (StreamListener listener : listeners) {
            ((StatusListener) listener).onScrubGeo(ParseUtil.getLong("user_id", scrubGeo)
                    , ParseUtil.getLong("up_to_status_id", scrubGeo));
        }

    }

    @Override
    public void onException(Exception e, StreamListener[] listeners) {
        for (StreamListener listener : listeners) {
            listener.onException(e);
        }
    }
}

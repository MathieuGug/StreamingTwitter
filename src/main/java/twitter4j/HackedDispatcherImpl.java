package twitter4j;

import twitter4j.conf.Configuration;

public class HackedDispatcherImpl implements Dispatcher {
    private static final Logger logger = Logger.getLogger(DispatcherImpl.class);
    private static final long SHUTDOWN_TIME = 5000;

    public HackedDispatcherImpl(final Configuration conf) {
    }

    @Override
    public synchronized void invokeLater(Runnable task) {
        task.run();
    }

    @Override
    public synchronized void shutdown() {
    }
}


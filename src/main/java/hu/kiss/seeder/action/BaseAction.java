package hu.kiss.seeder.action;

import hu.kiss.seeder.client.QbitorrentClient;
import hu.kiss.seeder.client.TorrentClientI;

public abstract class BaseAction implements Action {

    protected QbitorrentClient qClient;

    @Override
    public void init(QbitorrentClient client) {
        qClient = client;
    }
}

package hu.kiss.seeder.action;

import hu.kiss.seeder.data.DelugeTorrent;

public interface Action {

    public void init(Object... params);

    public void execute(DelugeTorrent torrent);
}

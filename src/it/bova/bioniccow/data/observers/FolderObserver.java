package it.bova.bioniccow.data.observers;

import it.bova.bioniccow.data.Folder;
import it.bova.bioniccow.data.DataObserver;

import java.util.List;
import java.util.Map;

public abstract class FolderObserver extends DataObserver<List<Folder>> {
	protected abstract void onDataChanged(List<Folder> folders);
}

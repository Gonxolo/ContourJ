package cl.scian.contourj.exporter;

import org.scijava.Context;

public abstract class AbstractDataExporter<T> implements DataExporter<T>{

    public AbstractDataExporter(Context context) {
        context.inject(this);
    }

}

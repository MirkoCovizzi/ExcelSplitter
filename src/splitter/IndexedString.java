package splitter;

import java.util.Comparator;

public class IndexedString{
    private int index;
    private String string;

    public IndexedString(int index, String string){
        this.index = index;
        this.string = string;
    }

    @Override
    public String toString() {
        return this.string;
    }

    public String getString(){
        return string;
    }

    public int getIndex() {
        return index;
    }

}

class IndexedStringComparator implements Comparator<IndexedString> {
    @Override
    public int compare(IndexedString a, IndexedString b) {
        if (a.getIndex() < b.getIndex()){
            return -1;
        } else if (a.getIndex() > b.getIndex()){
            return 1;
        }
        return 0;
    }
}

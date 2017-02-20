package splitter;

public class IndexedString {
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

    public void setString(String string){
        this.string = string;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }
}

package android.xwpeng.testipc.entity;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by xwpeng on 16-11-22.
 */

public class Book implements Parcelable{
    public int id;
    public String name;
    public double price;

    public Book(){}

    public Book(int id, String name, double price){
        this.id = id;
        this.name = name;
        this.price = price;
    }

    protected Book(Parcel in) {
       readFromParcel(in);
    }

    public void readFromParcel(Parcel in) {
        id = in.readInt();
        name = in.readString();
        price = in.readDouble();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(name);
        dest.writeDouble(price);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Book> CREATOR = new Creator<Book>() {
        @Override
        public Book createFromParcel(Parcel in) {
            return new Book(in);
        }

        @Override
        public Book[] newArray(int size) {
            return new Book[size];
        }
    };

    @Override
    public String toString() {
        return "Book{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", price=" + price +
                '}';
    }
}

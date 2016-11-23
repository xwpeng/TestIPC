package android.xwpeng.testipc.entity;

import android.os.Parcel;
import android.os.Parcelable;


/**
 * Created by xwpeng on 16-11-22.
 */

public class User2 implements Parcelable{
    public int userId;
    public String userName;
    public String gender;
    public Book book;

    public User2(){

    }

    protected User2(Parcel in) {
        userId = in.readInt();
        userName = in.readString();
        gender = in.readString();
        book = in.readParcelable(Book.class.getClassLoader());
    }

    public static final Creator<User2> CREATOR = new Creator<User2>() {
        @Override
        public User2 createFromParcel(Parcel in) {
            return new User2(in);
        }

        @Override
        public User2[] newArray(int size) {
            return new User2[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(userId);
        dest.writeString(userName);
        dest.writeString(gender);
        dest.writeParcelable(book, flags);
    }

    @Override
    public String toString() {
        return "User2{" +
                "userId=" + userId +
                ", userName='" + userName + '\'' +
                ", gender='" + gender + '\'' +
                ", book=" + book +
                '}';
    }
}

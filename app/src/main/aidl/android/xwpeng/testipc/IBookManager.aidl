// IBookManager.aidl
package android.xwpeng.testipc;
import android.xwpeng.testipc.entity.Book;
import android.xwpeng.testipc.entity.User2;
import android.xwpeng.testipc.IOnNewBookArrivedListener;

// Declare any non-default types here with import statements

interface IBookManager {
    /**
     * Demonstrates some basic types that you can use as parameters
     * and return values in AIDL.
     */
    void basicTypes(int anInt, long aLong, boolean aBoolean, float aFloat,
            double aDouble, String aString);

         List< Book> getBookList();
         Book addBookIn(in Book book);
         Book addBookOut(out Book book);
         Book addBookInOut(inout Book book);

         List<User2> getUserList();
         void addUser(in User2 u);

         void registerListener(in IOnNewBookArrivedListener listener);
         void unregisterListener(in IOnNewBookArrivedListener listener);
}

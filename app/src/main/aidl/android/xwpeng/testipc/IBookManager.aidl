// IBookManager.aidl
package android.xwpeng.testipc;
import android.xwpeng.testipc.entity.Book;
import android.xwpeng.testipc.entity.User2;

// Declare any non-default types here with import statements

interface IBookManager {
    /**
     * Demonstrates some basic types that you can use as parameters
     * and return values in AIDL.
     */
    void basicTypes(int anInt, long aLong, boolean aBoolean, float aFloat,
            double aDouble, String aString);

         List<Book> getBookList();
         void addBook(in Book b);

         List<User2> getUserList();
                  void addUser(in User2 u);
}

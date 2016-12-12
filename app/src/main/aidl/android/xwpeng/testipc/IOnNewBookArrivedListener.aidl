// IOnNewBookArrivedListener.aidl
package android.xwpeng.testipc;
import android.xwpeng.testipc.entity.Book;

// Declare any non-default types here with import statements

interface IOnNewBookArrivedListener {
            void onNewBookArrived(in Book newBook);
}

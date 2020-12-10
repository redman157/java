package company.ai.musicplayer.activiy

import android.app.AlertDialog
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import company.ai.musicplayer.R
import company.ai.musicplayer.extensions.handleViewVisibility


open class ActionBarCastActivity: AppCompatActivity() {
    private lateinit var mToolbar: Toolbar
    private var mToolbarInitialized = false
    override fun onBackPressed() {
        baseBackPressed()
    }

    open fun initializeToolbar(isShow: Boolean) {
        mToolbar = findViewById(R.id.toolbar)
        mToolbar.popupTheme = R.style.AppTheme_CustomActionBar
        mToolbar.title = this.getString(R.string.app_name)
        mToolbar.inflateMenu(R.menu.main)
        setSupportActionBar(mToolbar)
        if (isShow) {
            supportActionBar!!.show()
            mToolbar.handleViewVisibility(true)
        }else{
            supportActionBar!!.hide()
            mToolbar.handleViewVisibility(false)
        }
        mToolbarInitialized = true
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        super.onCreateOptionsMenu(menu)
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // If not handled by drawerToggle, home needs to be handled by returning to previous
        when (item.itemId) {
            android.R.id.home -> {
            }
            R.id.sleep_timer -> {
            }
            R.id.sync -> {

            }
            R.id.changeTheme -> {
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun baseBackPressed() {
        // Otherwise, it may return to the previous fragment stack
        val fragmentManager = supportFragmentManager
        if (fragmentManager.backStackEntryCount > 1) {
            fragmentManager.popBackStack()
        } else {
            // Lastly, it will rely on the system behavior for back
            val builder = AlertDialog.Builder(this)
            builder.setTitle("Bạn có muốn thoát App không ?")
            builder.setNegativeButton(
                "Không"
            ) { dialog, which -> dialog.cancel() }
            builder.setPositiveButton(
                "Có"
            ) { dialog, which -> System.exit(1) }
            builder.show()
        }
    }
}
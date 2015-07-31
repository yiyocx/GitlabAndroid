package yiyo.gitlabandroid.views.activities

import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import kotlinx.android.synthetic.activity_login.email_text_input_layout
import kotlinx.android.synthetic.activity_login.password_text_input_layout
import retrofit.RetrofitError
import yiyo.gitlabandroid.R
import yiyo.gitlabandroid.mvp.presenters.LoginPresenter
import yiyo.gitlabandroid.mvp.views.LoginView
import yiyo.gitlabandroid.utils.Configuration
import java.net.HttpURLConnection

class LoginActivity : AppCompatActivity(), LoginView {

    private var loginPresenter: LoginPresenter? = null
    private var progressDialog: ProgressDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super<AppCompatActivity>.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val configuration = Configuration(this@LoginActivity)

        if (configuration.isLoggedIn()) {
            // El usuario ya esta logueado. Se redirige a la Actividad Principal
            val intent = Intent(this@LoginActivity, javaClass<MainActivity>())
            startActivity(intent)
            finish()
        }

        loginPresenter = LoginPresenter(this)
    }

    override fun onStart() {
        super<AppCompatActivity>.onStart()
        loginPresenter!!.start()
    }

    override fun onStop() {
        super<AppCompatActivity>.onStop()
        loginPresenter!!.stop()
    }

    fun attemptLogin(view: View?) {
        // Get values at the time of the login attempt
        val username = email_text_input_layout.getEditText().getText().toString()
        val password = password_text_input_layout.getEditText().getText().toString()

        loginPresenter!!.login(username, password)
    }

    override fun showProgress() {
        progressDialog = ProgressDialog(this@LoginActivity)
        progressDialog!!.setMessage("Signing In")
        progressDialog!!.setIndeterminate(true)
        progressDialog!!.setCancelable(false)
        progressDialog!!.show()
    }

    override fun hideProgress() {
        progressDialog!!.dismiss()
    }

    override fun setupUsernameError(usernameError: String?) {
        email_text_input_layout.setError(usernameError)
    }

    override fun setupPasswordError(passwordError: String?) {
        password_text_input_layout.setError(passwordError)
    }

    override fun navigateToHome() {
        val intent = Intent(this@LoginActivity, javaClass<MainActivity>())
        startActivity(intent)
        finish()
    }

    override fun showConnectionError(error: RetrofitError) {
        val alertBuilder: AlertDialog.Builder

        if (error.getKind() === RetrofitError.Kind.NETWORK) {

            // Cuando no hay una conexión establecida
            alertBuilder = AlertDialog.Builder(this@LoginActivity).setTitle(getString(R.string.error_no_connection_title)).setMessage(getString(R.string.error_no_connection_description)).setPositiveButton(getString(R.string.action_retry), object : DialogInterface.OnClickListener {
                override fun onClick(dialog: DialogInterface, which: Int) {
                    dialog.dismiss()
                    this@LoginActivity.attemptLogin(null)
                }
            }).setNegativeButton(getString(R.string.action_cancel), object : DialogInterface.OnClickListener {
                override fun onClick(dialog: DialogInterface, which: Int) {
                    dialog.cancel()
                }
            }).setCancelable(false)
            alertBuilder.show()
        } else if (error.getResponse().getStatus() == HttpURLConnection.HTTP_UNAUTHORIZED) {

            alertBuilder = AlertDialog.Builder(this@LoginActivity).setTitle(getString(R.string.error_sign_in_failed_title)).setMessage(getString(R.string.error_sign_in_failed_description)).setPositiveButton(getString(R.string.action_accept), object : DialogInterface.OnClickListener {
                override fun onClick(dialog: DialogInterface, i: Int) {
                    dialog.dismiss()
                }
            })
            alertBuilder.show()
        } else if (error.getKind() === RetrofitError.Kind.UNEXPECTED) {

            alertBuilder = AlertDialog.Builder(this@LoginActivity).setTitle(getString(R.string.error_unexpected_title)).setMessage(getString(R.string.error_unexpected_description)).setPositiveButton("OK", object : DialogInterface.OnClickListener {
                override fun onClick(dialog: DialogInterface, i: Int) {
                    dialog.dismiss()
                }
            })
            alertBuilder.show()
        }
    }

    override fun getContext(): Context {
        return this@LoginActivity
    }
}

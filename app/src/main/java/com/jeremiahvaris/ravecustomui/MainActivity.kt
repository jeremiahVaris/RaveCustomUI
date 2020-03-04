package com.jeremiahvaris.ravecustomui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.flutterwave.raveandroid.*
import com.flutterwave.raveandroid.card.CardContract
import com.flutterwave.raveandroid.card.CardPresenter
import com.flutterwave.raveandroid.data.SavedCard
import com.flutterwave.raveandroid.responses.ChargeResponse
import com.flutterwave.raveandroid.responses.LookupSavedCardsResponse
import com.flutterwave.raveandroid.responses.RequeryResponse
import com.flutterwave.raveandroid.responses.SaveCardResponse
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.toast
import java.util.*

class MainActivity : AppCompatActivity(), CardContract.View {
    private lateinit var cvv: String
    private lateinit var expiryYear: String
    private lateinit var expiryMonth: String
    private lateinit var cardNumber: String
    private lateinit var email: String
    private val currency: String="NGN"
    private var amount: String = "10.0"
    private val otp: String?="12345"
    private var flwRef: String?=null
    private val onStaging = true

    private val encryptionKey: String
        get() = if (onStaging) "FLWSECK_TEST24a907495c60"
        else "7b52e2b832ecbb4451fe7b3b"
    private val publicKey: String
        get() = if (onStaging) "FLWPUBK_TEST-7ddb1c9cb4571aa27d588f468fb8c052-X"
        else "FLWPUBK-aec2b6c6cfe500854a21a0808f1ca280-X"

    lateinit var presenter: CardPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val raver = RavePayManager(this)
            .onStagingEnv(onStaging).initializeNoUi()
        presenter = CardPresenter(this, this, raver.appComponent)

        setUpDefaultValues()


        pay_button.setOnClickListener {
            amount = amountEt.text.toString()
            email = emailEt.text.toString()
            cardNumber = cardNoEt.text.toString()
            expiryMonth = cardExpiryEt.text.toString().substring(0,2)
            expiryYear = cardExpiryEt.text.toString().substring(3)
            cvv = cvvEt.text.toString()
            pay()
        }
    }

    private fun setUpDefaultValues() {
        amountEt.setText("10")
        emailEt.setText("chairman@bossman.com")
        cardNoEt.setText("5531886652142950")
        cardExpiryEt.setText("06/20")
        cvvEt.setText("123")
    }

    private fun pay() {
        val builder = PayloadBuilder()
        builder.setAmount(amount)
            .setCardno(amount)
            .setCountry("NG")
            .setCurrency("NGN")
            .setCvv(cvv)
            .setEmail(email)
            .setFirstname("Wuraola")
            .setLastname("Benson")
            .setIP("")
            .setTxRef("1")
            .setExpiryyear(expiryYear)
            .setExpirymonth(expiryMonth)
            .setPBFPubKey(publicKey)
            .setDevice_fingerprint("")

        val body = builder.createPayload()

        presenter.chargeCard(body, encryptionKey)
    }

    override fun onCardSaveSuccessful(
        response: SaveCardResponse?,
        responseAsJSONString: String?,
        phoneNumber: String?
    ) {
        toast("onCardSaveSuccessful called")
    }

    override fun showToast(message: String?) {
        toast("message")

    }

    override fun onNoAuthInternationalSuggested(payload: Payload?) {
        toast("onNoAuthInternationalSuggested called")
    }

    override fun onRequerySuccessful(
        response: RequeryResponse?,
        responseAsJSONString: String?,
        flwRef: String?
    ) {
        val wasTxSuccessful: Boolean = TransactionStatusChecker(Gson())
            .getTransactionStatus(
                amount,
                currency,
                responseAsJSONString
            )

        if (wasTxSuccessful) {
            onPaymentSuccessful(
                response!!.status,
                flwRef,
                responseAsJSONString,
                null
            )
        } else {
            onPaymentFailed(response!!.status, responseAsJSONString)
        }
        toast("onRequerySuccessful called")
    }

    override fun onPhoneNumberValidated(phoneNumber: String?) {
        toast("onPhoneNumberValidated called")

    }

    override fun onAmountValidated(amountToSet: String?, visibility: Int) {
        toast("onAmountValidated called")

    }

    override fun showFetchFeeFailed(s: String?) {
        s?.let { toast(it) }
    }

    override fun onVBVAuthModelUsed(authUrlCrude: String?, flwRef: String?) {
        toast("onVBVAuthModelUsed called")
    }

    override fun showFieldError(viewID: Int, message: String?, viewtype: Class<*>?) {
        toast("showFieldError called")
    }

    override fun onTokenRetrievalError(s: String?) {
        toast("onTokenRetrievalError called")
    }

    override fun onPaymentFailed(status: String?, responseAsString: String?) {
        toast("onPaymentFailed called")
        showSnackBar("Payment successful",true)
    }

    override fun onValidateError(message: String?) {
        toast("onValidateError called")
    }

    override fun onCardSaveFailed(message: String?, responseAsJSONString: String?) {
        toast("onCardSaveFailed called")
    }

    override fun onEmailValidated(emailToSet: String?, visibility: Int) {
        toast("onEmailValidated called")
    }

    override fun showSavedCardsLayout(savedCardsList: MutableList<SavedCard>?) {
        toast("showSavedCardsLayout called")
    }

    override fun onNoAuthUsed(flwRef: String?, publicKey: String?) {
        toast("onNoAuthUsed called")
    }

    override fun onSendRaveOtpFailed(message: String?, responseAsJSONString: String?) {
        toast("onSendRaveOtpFailed called")
    }

    override fun showSavedCards(cards: MutableList<SavedCard>?) {
        toast("showSavedCards called")
    }

    override fun onValidateCardChargeFailed(flwRef: String?, responseAsJSON: String?) {
        toast("onValidateCardChargeFailed called")
    }

    override fun onPaymentError(message: String?) {
        message?.let { toast(it) }
    }

    override fun showOTPLayout(flwRef: String?, chargeResponseMessage: String?) {
        this.flwRef = flwRef
        toast("showOTPLayout called")
        presenter.validateCardCharge(flwRef, otp, publicKey)
    }

    override fun onChargeTokenComplete(response: ChargeResponse?) {
        toast("onChargeTokenComplete called")
    }

    override fun onPaymentSuccessful(
        status: String?,
        flwRef: String?,
        responseAsString: String?,
        ravePayInitializer: RavePayInitializer?
    ) {
        toast("onPaymentSuccessful called")
        showSnackBar("Payment successful",true)
    }

    override fun showCardSavingOption(b: Boolean) {
        toast("showCardSavingOption called")
    }

    override fun showProgressIndicator(active: Boolean) {
        toast("showProgressIndicator called")
    }

    override fun setHasSavedCards(b: Boolean) {
        toast("setHasSavedCards called")
    }

    override fun onAVSVBVSecureCodeModelUsed(authurl: String?, flwRef: String?) {
        toast("onAVSVBVSecureCodeModelUsed called")
    }

    override fun onValidationSuccessful(dataHashMap: HashMap<String, ViewObject>?) {
        toast("onValidationSuccessful called")
    }

    override fun displayFee(charge_amount: String?, payload: Payload?, why: Int) {
        toast("displayFee called")
    }

    override fun onChargeCardSuccessful(response: ChargeResponse?) {
        toast("onChargeCardSuccessful called")
    }

    override fun onAVS_VBVSECURECODEModelSuggested(payload: Payload?) {
        toast("onAVS_VBVSECURECODEModelSuggested called")
    }

    override fun onTokenRetrieved(flwRef: String?, cardBIN: String?, token: String?) {
        toast("onTokenRetrieved called")
    }

    override fun onLookupSavedCardsFailed(
        message: String?,
        responseAsJSONString: String?,
        verifyResponseAsJSONString: String?
    ) {
        toast("onLookupSavedCardsFailed called")
    }

    override fun onValidateSuccessful(message: String?, responseAsString: String?) {
        toast("onValidateSuccessful called")
        presenter.requeryTx(flwRef, publicKey)

    }

    override fun onLookupSavedCardsSuccessful(
        response: LookupSavedCardsResponse?,
        responseAsJSONString: String?,
        verifyResponseAsJSONString: String?
    ) {
        toast("onLookupSavedCardsSuccessful called")
    }

    override fun showOTPLayoutForSavedCard(payload: Payload?, authInstruction: String?) {
        toast("showOTPLayoutForSavedCard called")
    }

    override fun onPinAuthModelSuggested(payload: Payload?) {
        toast("onPinAuthModelSuggested called")
        presenter.chargeCardWithSuggestedAuthModel(
            payload,
            "3310",
            RaveConstants.PIN,
            encryptionKey
        )
    }

    private fun showSnackBar(message: String, static: Boolean) {
        var snackBarLength = Snackbar.LENGTH_SHORT

        if (static) snackBarLength = Snackbar.LENGTH_INDEFINITE

        val mySnackbar = Snackbar.make(
            pay_button,
            message, snackBarLength
        )
        mySnackbar.setActionTextColor(ContextCompat.getColor(this, R.color.white))

        mySnackbar.setAction("OK") {
            mySnackbar.dismiss()

        }

        mySnackbar.show()
    }
}
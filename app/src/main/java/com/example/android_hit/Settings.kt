package com.example.android_hit

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.android_hit.room.TransactionDB
import com.example.android_hit.room.TransactionEntity
import com.example.android_hit.utils.TokenManager
import com.example.android_hit.utils.UserManager
import org.apache.poi.hssf.usermodel.HSSFWorkbook
import org.apache.poi.ss.usermodel.IndexedColors
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.io.File
import java.io.FileOutputStream

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [Settings.newInstance] factory method to
 * create an instance of this fragment.
 */
class Settings : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var sharedPref : TokenManager
    private lateinit var logoutButton : Button
    private lateinit var emailTextView : TextView
    private lateinit var user: UserManager
    private lateinit var saveButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    @SuppressLint("UseRequireInsteadOfGet", "MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_settings, container, false)
        // Inflate the layout for this fragment
        sharedPref = this.context?.let { TokenManager(it) }!!
        user = this.context?.let { UserManager(it) }!!
        logoutButton = view.findViewById(R.id.logoutButton)
        emailTextView = view.findViewById(R.id.emailTextView)
        saveButton = view.findViewById(R.id.saveTransactionButton)
        emailTextView.text = user.getEmail("EMAIL")
        val database = TransactionDB.getInstance(requireContext())
        val transactionDao = database.transactionDao
        val transactions = transactionDao.getAllTransaction()
        Log.e("SET","masuk sini")

        logoutButton.setOnClickListener {
            showConfirmationDialog()

        }
        saveButton.setOnClickListener {
            val fileFormats = arrayOf("xls", "xlsx")

            AlertDialog.Builder(requireContext()).apply {
                setTitle("Choose file format")
                setItems(fileFormats) { dialog, which ->
                    val fileFormat = fileFormats[which]
                    val documentsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)
                    val documentsFolder = File(documentsDir, "Bondoman-Transaction")
                    if (!documentsFolder.exists()) {
                        documentsFolder.mkdirs()
                    }
                    val title = "transactions.$fileFormat"
                    val file = File(documentsFolder, title)
                    val fileOutputStream = FileOutputStream(file)
                    saveTransactionsToExcel(transactions, fileFormat, fileOutputStream)
                    fileOutputStream.close()
                    Toast.makeText(requireContext(), "Transaksi berhasil disimpan", Toast.LENGTH_SHORT).show()
                }
                setNegativeButton("Cancel") { dialog, _ ->
                    dialog.dismiss()
                }
            }.create().show()
        }


        return view
    }
    private fun showConfirmationDialog() {
        val alertDialogBuilder = AlertDialog.Builder(this.context)
        alertDialogBuilder.apply {
            setTitle("Confirmation")
            setMessage("Are you sure you want to logout?")
            setPositiveButton("Yes") { dialogInterface: DialogInterface, _: Int ->
                Log.e("SET","masuk sini 2")
                sharedPref.deleteToken()
                goToStart()
                dialogInterface.dismiss()
            }
            setNegativeButton("No") { dialogInterface: DialogInterface, _: Int ->
                dialogInterface.dismiss()
            }
            setCancelable(false)
        }

        val alertDialog = alertDialogBuilder.create()
        alertDialog.show()
    }

    private fun goToStart(){
        val intent = Intent(activity, LoginActivity::class.java)
        startActivity(intent)
    }



    private fun saveTransactionsToExcel(transactions: List<TransactionEntity>, fileFormat:String, outputStream: FileOutputStream) {

        val workbook = if (fileFormat == "xlsx") XSSFWorkbook() else HSSFWorkbook()

        val sheet = workbook.createSheet("Transactions")
        val cellStyle = workbook.createCellStyle()
        cellStyle.fillForegroundColor = IndexedColors.LIGHT_YELLOW.getIndex()
        cellStyle.fillPattern = org.apache.poi.ss.usermodel.FillPatternType.SOLID_FOREGROUND

        val headerRow = sheet.createRow(0)
        headerRow.createCell(0).setCellValue("ID")
        headerRow.createCell(1).setCellValue("Title")
        headerRow.createCell(2).setCellValue("Amount")
        headerRow.createCell(3).setCellValue("Category")
        headerRow.createCell(4).setCellValue("Location")
        headerRow.createCell(5).setCellValue("Timestamp")

        transactions.forEachIndexed { index, transaction ->
            val row = sheet.createRow(index + 1)
            row.createCell(0).setCellValue(transaction.id?.toDouble() ?: 0.0)
            row.createCell(1).setCellValue(transaction.title)
            row.createCell(2).setCellValue(transaction.amount.toDouble())
            row.createCell(3).setCellValue(transaction.category)
            row.createCell(4).setCellValue(transaction.location)
            row.createCell(5).setCellValue(transaction.timestamp)
        }
        // Menyimpan workbook ke file
        workbook.write(outputStream)
        workbook.close()


    }




}
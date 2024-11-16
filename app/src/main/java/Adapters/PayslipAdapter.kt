import Models.Payslip
import android.content.Context
import android.graphics.Color
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.os.Environment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.nexatech.staffsyncv3.R
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory

class PayslipAdapter(
    private val context: Context,
    private val payslipList: List<Payslip>
) : RecyclerView.Adapter<PayslipAdapter.PayslipViewHolder>() {

    inner class PayslipViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvMonth: TextView = itemView.findViewById(R.id.tvMonth)
        val tvSalary: TextView = itemView.findViewById(R.id.tvSalary)
        val tvBonus: TextView = itemView.findViewById(R.id.tvBonus)
        val tvTotal: TextView = itemView.findViewById(R.id.tvTotal)
        val btnGeneratePdf: Button = itemView.findViewById(R.id.btnGeneratePdf)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PayslipViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_payslip, parent, false)
        return PayslipViewHolder(view)
    }

    override fun onBindViewHolder(holder: PayslipViewHolder, position: Int) {
        val payslip = payslipList[position]

        holder.tvMonth.text = "${payslip.month}"
        holder.tvSalary.text = "Salary: ${payslip.salary}"
        holder.tvBonus.text = "Bonus: ${payslip.bonus}"
        holder.tvTotal.text = "Total: ${payslip.total}"

        // Set up PDF generation on button click
        holder.btnGeneratePdf.setOnClickListener {
            // Check for storage permission
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(
                    (context as Activity), arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), 1
                )
            } else {
                createPdfDocument(payslip)
            }
        }
    }

    override fun getItemCount(): Int = payslipList.size

    private fun createPdfDocument(payslip: Payslip) {
        val pdfDocument = PdfDocument()
        val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create() // A4 size
        val page = pdfDocument.startPage(pageInfo)
        val canvas = page.canvas
        val paint = Paint().apply {
            color = Color.BLACK
            textSize = 14f
        }
        val linePaint = Paint().apply {
            color = Color.LTGRAY
            strokeWidth = 2f
        }

        // Load and position the logo in the top right corner
        val logoBitmap = BitmapFactory.decodeResource(context.resources, R.drawable.logo_new_no_text) // Replace with actual logo resource
        val logoScaledWidth = 100 // Adjust the width of the logo as needed
        val logoScaledHeight = logoBitmap.height * logoScaledWidth / logoBitmap.width // Maintain aspect ratio
        val scaledLogo = Bitmap.createScaledBitmap(logoBitmap, logoScaledWidth, logoScaledHeight, true)
        val logoX = pageInfo.pageWidth - logoScaledWidth - 40f // Adjust padding if needed
        val logoY = 40f // Padding from the top
        canvas.drawBitmap(scaledLogo, logoX, logoY, null)

        // Set Y position and X positions for the text layout
        var yPosition = logoY + logoScaledHeight + 50 // Start below the logo
        val labelX = 70f
        val valueX = pageInfo.pageWidth - 70f // Align values to the right

        // Draw payslip title
        paint.textSize = 18f
        paint.isFakeBoldText = true
        canvas.drawText("Payslip for Month: ${payslip.month}", labelX, yPosition.toFloat(), paint)
        yPosition += 60

        // Reset text style for regular items
        paint.textSize = 14f
        paint.isFakeBoldText = false

        // Draw employee ID
        canvas.drawText("Employee ID: ${payslip.employee_id}", labelX, yPosition.toFloat(), paint)
        yPosition += 40

        // Draw salary
        canvas.drawText("Salary:", labelX, yPosition.toFloat(), paint)
        canvas.drawText("${payslip.salary}", valueX, yPosition.toFloat(), paint)
        yPosition += 40

        // Draw bonus
        canvas.drawText("Bonus:", labelX, yPosition.toFloat(), paint)
        canvas.drawText("${payslip.bonus}", valueX, yPosition.toFloat(), paint)
        yPosition += 20
        canvas.drawLine(labelX, yPosition.toFloat(), valueX, yPosition.toFloat(), linePaint) // Divider line
        yPosition += 40

        // Draw total
        paint.isFakeBoldText = true
        canvas.drawText("Total:", labelX, yPosition.toFloat(), paint)
        canvas.drawText("${payslip.total}", valueX, yPosition.toFloat(), paint)

        pdfDocument.finishPage(page)

        // Save the PDF to the Downloads folder
        val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        val fileName = "Payslip_${payslip.month}.pdf"
        val file = File(downloadsDir, fileName)

        try {
            pdfDocument.writeTo(FileOutputStream(file))
            Toast.makeText(context, "PDF generated: ${file.absolutePath}", Toast.LENGTH_LONG).show()
        } catch (e: IOException) {
            Toast.makeText(context, "Error generating PDF: ${e.message}", Toast.LENGTH_LONG).show()
        } finally {
            pdfDocument.close()
        }
    }


}

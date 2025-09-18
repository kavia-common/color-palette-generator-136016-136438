package org.example.app

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.animation.AlphaAnimation
import android.widget.ImageButton
import android.widget.Switch
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import org.example.app.model.HslColor
import org.example.app.ui.PaletteViewModel

/**
 PUBLIC_INTERFACE
 MainActivity
 This is the entry point activity that displays a stack of 5 color tiles showing both HSL and HEX values,
 enables copying color codes, toggling theme (light/dark), and generating new palettes with an animated transition.

 Parameters: none from caller
 Returns: none
 */
class MainActivity : AppCompatActivity() {

    private lateinit var viewModel: PaletteViewModel
    private lateinit var recycler: RecyclerView
    private lateinit var adapter: PaletteAdapter
    private lateinit var fab: FloatingActionButton
    private lateinit var loadingText: TextView
    private lateinit var themeSwitch: Switch

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        viewModel = ViewModelProvider(this)[PaletteViewModel::class.java]

        recycler = findViewById(R.id.paletteRecycler)
        fab = findViewById(R.id.generateFab)
        loadingText = findViewById(R.id.loadingText)
        themeSwitch = findViewById(R.id.themeSwitch)

        adapter = PaletteAdapter(
            onCopy = { color, format ->
                handleCopy(color, format)
            }
        )
        recycler.adapter = adapter
        recycler.layoutManager = LinearLayoutManager(this)
        recycler.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL).apply {
            // subtle separator; actual spacing handled by item layout and margins
        })

        // Observe state
        viewModel.palette.observe(this) { colors ->
            adapter.submit(colors)
            crossfade(recycler, true)
        }
        viewModel.isLoading.observe(this) { isLoading ->
            loadingText.isVisible = isLoading
            if (isLoading) {
                crossfade(recycler, false)
            }
        }

        // Generate initial palette
        viewModel.generate(initial = true)

        fab.setOnClickListener {
            viewModel.generate()
        }
        fab.contentDescription = getString(R.string.cd_generate_palette)

        themeSwitch.setOnCheckedChangeListener { _, isChecked ->
            AppCompatDelegate.setDefaultNightMode(
                if (isChecked) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO
            )
            themeSwitch.contentDescription = if (isChecked) {
                getString(R.string.cd_theme_switch_dark_on)
            } else {
                getString(R.string.cd_theme_switch_dark_off)
            }
        }
        // Reflect current mode in switch
        themeSwitch.isChecked = (resources.configuration.uiMode and
                android.content.res.Configuration.UI_MODE_NIGHT_MASK) == android.content.res.Configuration.UI_MODE_NIGHT_YES
    }

    private fun crossfade(view: View, visible: Boolean) {
        val anim = AlphaAnimation(if (visible) 0f else 1f, if (visible) 1f else 0f).apply {
            duration = 220
            fillAfter = true
        }
        view.startAnimation(anim)
        view.isVisible = visible
    }

    private fun handleCopy(color: HslColor, format: CopyFormat) {
        val text = when (format) {
            CopyFormat.HSL -> color.toHslString()
            CopyFormat.HEX -> color.toHex()
        }
        val clipMgr = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        clipMgr.setPrimaryClip(ClipData.newPlainText("color", text))
        Snackbar.make(findViewById(R.id.root), getString(R.string.copied_value, text), Snackbar.LENGTH_SHORT).show()
    }
}

enum class CopyFormat { HSL, HEX }

/**
 * RecyclerView Adapter for displaying color tiles with accessible controls and minimalist visuals.
 */
private class PaletteAdapter(
    private val onCopy: (HslColor, CopyFormat) -> Unit
) : RecyclerView.Adapter<PaletteViewHolder>() {

    private val items = mutableListOf<HslColor>()

    // PUBLIC_INTERFACE
    fun submit(colors: List<HslColor>) {
        items.clear()
        items.addAll(colors)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: android.view.ViewGroup, viewType: Int): PaletteViewHolder {
        val view = layoutInflater(parent.context).inflate(R.layout.item_color_tile, parent, false)
        return PaletteViewHolder(view, onCopy)
    }

    override fun onBindViewHolder(holder: PaletteViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    private fun layoutInflater(context: Context) = android.view.LayoutInflater.from(context)
}

/**
 * ViewHolder for a single color tile.
 */
private class PaletteViewHolder(
    itemView: View,
    private val onCopy: (HslColor, CopyFormat) -> Unit
) : RecyclerView.ViewHolder(itemView) {

    private val colorContainer: View = itemView.findViewById(R.id.colorContainer)
    private val hslText: TextView = itemView.findViewById(R.id.hslText)
    private val hexText: TextView = itemView.findViewById(R.id.hexText)
    private val copyHslBtn: ImageButton = itemView.findViewById(R.id.copyHsl)
    private val copyHexBtn: ImageButton = itemView.findViewById(R.id.copyHex)

    fun bind(color: HslColor) {
        colorContainer.setBackgroundColor(color.toColorInt())
        hslText.text = color.toHslString()
        hexText.text = color.toHex()

        // Accessibility
        colorContainer.contentDescription = itemView.context.getString(
            R.string.cd_color_tile, color.toHslString(), color.toHex()
        )
        copyHslBtn.contentDescription = itemView.context.getString(R.string.cd_copy_hsl, color.toHslString())
        copyHexBtn.contentDescription = itemView.context.getString(R.string.cd_copy_hex, color.toHex())

        copyHslBtn.setOnClickListener { onCopy(color, CopyFormat.HSL) }
        copyHexBtn.setOnClickListener { onCopy(color, CopyFormat.HEX) }
    }
}

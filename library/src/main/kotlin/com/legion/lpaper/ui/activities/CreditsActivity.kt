/*
 * Copyright (c) 2019. Raj Kale
 *
 * Licensed under the CreativeCommons Attribution-ShareAlike
 * 4.0 International License. You may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 *    http://creativecommons.org/licenses/by-sa/4.0/legalcode
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.legion.lpaper.ui.activities

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.annotation.StringRes
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import ca.allanwang.kau.utils.openLink
import com.bumptech.glide.Glide
import com.pluscubed.recyclerfastscroll.RecyclerFastScroller
import de.psdev.licensesdialog.LicenseResolver
import de.psdev.licensesdialog.LicensesDialog
import de.psdev.licensesdialog.licenses.License
import com.legion.lpaper.R
import com.legion.lpaper.helpers.utils.FL
import com.legion.lpaper.helpers.utils.LPaperKonfigs
import com.legion.lpaper.ui.adapters.CreditsAdapter
import com.legion.lpaper.ui.adapters.viewholders.Credit
import com.legion.lpaper.ui.widgets.CustomToolbar
import com.legion.lpaper.ui.widgets.EmptyViewRecyclerView
import jahirfiquitiva.libs.kext.extensions.bind
import jahirfiquitiva.libs.kext.extensions.dividerColor
import jahirfiquitiva.libs.kext.extensions.getActiveIconsColorFor
import jahirfiquitiva.libs.kext.extensions.getPrimaryTextColorFor
import jahirfiquitiva.libs.kext.extensions.getSecondaryTextColorFor
import jahirfiquitiva.libs.kext.extensions.hasContent
import jahirfiquitiva.libs.kext.extensions.isInHorizontalMode
import jahirfiquitiva.libs.kext.extensions.primaryColor
import jahirfiquitiva.libs.kext.extensions.stringArray
import jahirfiquitiva.libs.kext.extensions.tint
import jahirfiquitiva.libs.kext.ui.activities.ThemedActivity

open class CreditsActivity : ThemedActivity<LPaperKonfigs>() {
    
    override val prefs: LPaperKonfigs by lazy { LPaperKonfigs(this) }
    override fun lightTheme(): Int = R.style.LightTheme
    override fun darkTheme(): Int = R.style.DarkTheme
    override fun amoledTheme(): Int = R.style.AmoledTheme
    override fun transparentTheme(): Int = R.style.TransparentTheme
    
    override fun autoTintStatusBar(): Boolean = true
    override fun autoTintNavigationBar(): Boolean = true
    
    private val toolbar: CustomToolbar? by bind(R.id.toolbar)
    private val recyclerView: EmptyViewRecyclerView? by bind(R.id.list_rv)
    private val fastScroller: RecyclerFastScroller? by bind(R.id.fast_scroller)
    
    @SuppressLint("MissingSuperCall")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_credits)
        
        registerExtraLicenses()
        
        toolbar?.bindToActivity(this)
        supportActionBar?.title = getString(R.string.about)
        
        val refreshLayout: SwipeRefreshLayout? by bind(R.id.swipe_to_refresh)
        refreshLayout?.isEnabled = false
        
        recyclerView?.itemAnimator = DefaultItemAnimator()
        recyclerView?.state = EmptyViewRecyclerView.State.LOADING
        
        val layoutManager =
            GridLayoutManager(this, if (isInHorizontalMode) 2 else 1, RecyclerView.VERTICAL, false)
        recyclerView?.layoutManager = layoutManager
        
        val adapter = CreditsAdapter(getDashboardTitle(), Glide.with(this), buildCreditsList())
        adapter.setLayoutManager(layoutManager)
        recyclerView?.adapter = adapter
        
        recyclerView?.let { fastScroller?.attachRecyclerView(it) }
        
        try {
            adapter.collapseSection(2)
            adapter.collapseSection(3)
        } catch (ignored: Exception) {
        }
        
        recyclerView?.state = EmptyViewRecyclerView.State.NORMAL
    }
    
    @StringRes
    open fun getDashboardTitle() = R.string.lpaper_dashboard
    
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
       
        toolbar?.tint(
            getPrimaryTextColorFor(primaryColor),
            getSecondaryTextColorFor(primaryColor),
            getActiveIconsColorFor(primaryColor))
        return super.onCreateOptionsMenu(menu)
    }
    
    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        item?.let {
            when (it.itemId) {
                android.R.id.home -> finish()
                
                else -> {
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }
    
    open fun getTranslationSite(): String = "http://j.mp/Trnsl8LPaper"
    
    private fun registerExtraLicenses() {
        val ccLicense = object : License() {
            override fun getName(): String =
                "CreativeCommons Attribution-ShareAlike 4.0 International License"
            
            override fun readSummaryTextFromResources(context: Context): String =
                readFullTextFromResources(context)
            
            override fun readFullTextFromResources(context: Context): String =
                "\tLicensed under the CreativeCommons Attribution-ShareAlike\n\t4.0 " +
                    "International License. You may not use this file except in compliance \n" +
                    "\twith the License. You may obtain a copy of the License at\n\n\t\t" +
                    "http://creativecommons.org/licenses/by-sa/4.0/legalcode\n\n" +
                    "\tUnless required by applicable law or agreed to in writing, software\n" +
                    "\tdistributed under the License is distributed on an \"AS IS\" BASIS,\n" +
                    "\tWITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.\n" +
                    "\tSee the License for the specific language governing permissions and\n" +
                    "\tlimitations under the License."
            
            override fun getVersion(): String = "4.0"
            
            override fun getUrl(): String =
                "http://creativecommons.org/licenses/by-sa/4.0/legalcode"
        }
        
        val eclLicense = object : License() {
            override fun getName(): String = "Educational Community License v2.0"
            
            override fun readSummaryTextFromResources(context: Context): String =
                readFullTextFromResources(context)
            
            override fun readFullTextFromResources(context: Context): String =
                "The Educational Community License version 2.0 (\"ECL\") consists of the " +
                    "Apache 2.0 license, modified to change the scope of the patent grant in " +
                    "section 3 to be specific to the needs of the education communities " +
                    "using this license.\n\nLicensed under the Apache License, Version 2.0 " +
                    "(the \"License\");\n" + "you may not use this file except in compliance with " +
                    "the License.\nYou may obtain a copy of the License at\n\n\t" +
                    "http://www.apache.org/licenses/LICENSE-2.0\n\nUnless required by applicable " +
                    "law or agreed to in writing, software\ndistributed under the License is " +
                    "distributed on an \"AS IS\" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY " +
                    "KIND, either express or implied.\nSee the License for the specific " +
                    "language governing permissions and\nlimitations under the License."
            
            override fun getVersion(): String = "2.0"
            
            override fun getUrl(): String = "https://opensource.org/licenses/ECL-2.0"
        }
        LicenseResolver.registerLicense(ccLicense)
        LicenseResolver.registerLicense(eclLicense)
    }
    
    private fun buildCreditsList(): ArrayList<Credit> {
        val list = ArrayList<Credit>()
        
        try {
            val titles = stringArray(R.array.credits_titles) ?: arrayOf("")
            val descriptions = stringArray(R.array.credits_descriptions) ?: arrayOf("")
            val photos = stringArray(R.array.credits_photos) ?: arrayOf("")
            val buttons = stringArray(R.array.credits_buttons) ?: arrayOf("")
            val links = stringArray(R.array.credits_links) ?: arrayOf("")
            
            if (descriptions.size == titles.size && photos.size == titles.size) {
                for (it in 0 until titles.size) {
                    val title = titles[it]
                    if (title.hasContent()) {
                        list += Credit(
                            title, photos[it], Credit.Type.CREATOR,
                            description = descriptions[it],
                            buttonsTitles = buttons[it].split("|"),
                            buttonsLinks = links[it].split("|"))
                    }
                }
            }
            
            list.add(
                Credit(
                    "Raj Kale", RAJ_PHOTO_URL, Credit.Type.DASHBOARD,
                    description = getString(R.string.dashboard_copyright),
                    buttonsTitles = RAJ_BUTTONS.split("|"),
                    buttonsLinks = RAJ_LINKS.split("|")))
            
            list.add(
                Credit(
                    "Prashant", PRASHANT_PHOTO_URL, Credit.Type.DASHBOARD,
                    description = getString(R.string.PRASHANT_description),
                    buttonsTitles = PRASHANT_BUTTONS.split("|"),
                    buttonsLinks = PRASHANT_LINKS.split("|")))

    } catch (e: Exception) {
            FL.e(e.message)
        }
        return list
    }

    private companion object {
        const val RAJ_PHOTO_URL =
            "https://avatars2.githubusercontent.com/u/26388747?s=400&v=4"
        const val RAJ_BUTTONS = "Website|Telegram"
        const val RAJ_LINKS = "https://legionos.ml/|https://t.me/rajkale99"
        
        const val PRASHANT_PHOTO_URL = "https://avatars0.githubusercontent.com/u/48080816?s=400&v=4"
        const val PRASHANT_BUTTONS = "Website|Telegram"
        const val PRASHANT_LINKS = "https://legionos.ml|https://t.me/prashantt41"
       
    }
}

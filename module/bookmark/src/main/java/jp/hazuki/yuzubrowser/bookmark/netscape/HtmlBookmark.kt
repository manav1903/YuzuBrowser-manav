/*
 * Copyright (C) 2017-2021 Hazuki
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package jp.hazuki.yuzubrowser.bookmark.netscape

import android.content.Context
import android.net.Uri
import jp.hazuki.yuzubrowser.bookmark.item.BookmarkFolder
import jp.hazuki.yuzubrowser.bookmark.repository.BookmarkManager
import jp.hazuki.yuzubrowser.favicon.FaviconManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException

suspend fun BookmarkManager.importHtmlBookmark(
    context: Context,
    uri: Uri,
    faviconManager: FaviconManager,
    folder: BookmarkFolder,
): Int = withContext(Dispatchers.IO) {
    try {
        context.contentResolver.openInputStream(uri)?.use {
            val parser = NetscapeBookmarkParser(folder, faviconManager)
            parser.parse(it)
            save()
            return@withContext 1
        }
    } catch (e: NetscapeBookmarkException) {
        return@withContext -1
    } catch (e: IOException) {
        e.printStackTrace()
    }
    return@withContext 0
}

suspend fun BookmarkManager.exportHtmlBookmark(
    context: Context,
    uri: Uri,
): Boolean = withContext(Dispatchers.IO) {
    try {
        context.contentResolver.openOutputStream(uri)?.apply {
            bufferedWriter().use {
                val creator = NetscapeBookmarkCreator(root)
                creator.create(it)
                return@withContext true
            }
        }
    } catch (e: IOException) {
        e.printStackTrace()
    }
    return@withContext false
}

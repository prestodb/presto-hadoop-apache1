/*
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
package org.apache.hadoop.fs;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.util.ReflectionUtils;

import java.io.IOException;
import java.lang.reflect.Field;
import java.net.URI;
import java.util.Map;

public final class PrestoFileSystemCache
        extends FileSystem.Cache
{
    private final Map<Key, FileSystem> map;

    @SuppressWarnings("unchecked")
    public PrestoFileSystemCache()
    {
        map = getPrivateMap(this);
    }

    @Override
    FileSystem get(URI uri, Configuration conf)
            throws IOException
    {
        return getInternal(uri, conf, new Key(uri, conf));
    }

    private synchronized FileSystem getInternal(URI uri, Configuration conf, Key key)
            throws IOException
    {
        FileSystem fs = map.get(key);
        if (fs != null) {
            return fs;
        }

        fs = createFileSystem(uri, conf);
        map.put(key, fs);
        return fs;
    }

    private static FileSystem createFileSystem(URI uri, Configuration conf)
            throws IOException
    {
        Class<?> clazz = conf.getClass("fs." + uri.getScheme() + ".impl", null);
        if (clazz == null) {
            throw new IOException("No FileSystem for scheme: " + uri.getScheme());
        }
        FileSystem fs = (FileSystem) ReflectionUtils.newInstance(clazz, conf);
        fs.initialize(uri, conf);
        return fs;
    }

    @SuppressWarnings("unchecked")
    private static Map<Key, FileSystem> getPrivateMap(FileSystem.Cache cache)
    {
        try {
            Field field = FileSystem.Cache.class.getDeclaredField("map");
            field.setAccessible(true);
            return (Map<Key, FileSystem>) field.get(cache);
        }
        catch (ReflectiveOperationException e) {
            throw new AssertionError(e);
        }
    }
}

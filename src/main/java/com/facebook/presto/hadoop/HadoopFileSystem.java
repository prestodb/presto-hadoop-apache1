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
package com.facebook.presto.hadoop;

import org.apache.hadoop.fs.BlockLocation;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.LocatedFileStatus;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.RemoteIterator;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Iterator;

import static java.util.Arrays.asList;

public final class HadoopFileSystem
{
    private HadoopFileSystem() {}

    public static RemoteIterator<LocatedFileStatus> listLocatedStatus(final FileSystem fs, Path path)
            throws IOException
    {
        final FileStatus[] files = fs.listStatus(path);
        if (files == null) {
            throw new FileNotFoundException("Path does not exist: " + path);
        }

        return new RemoteIterator<LocatedFileStatus>()
        {
            private final Iterator<FileStatus> iterator = asList(files).iterator();

            @Override
            public boolean hasNext()
            {
                return iterator.hasNext();
            }

            @Override
            public LocatedFileStatus next()
                    throws IOException
            {
                FileStatus file = iterator.next();
                BlockLocation[] blocks = null;
                if (!file.isDir()) {
                    blocks = fs.getFileBlockLocations(file, 0, file.getLen());
                }
                return new LocatedFileStatus(file, blocks);
            }
        };
    }
}

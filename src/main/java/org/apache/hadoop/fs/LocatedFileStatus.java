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

import java.io.IOException;

public class LocatedFileStatus
        extends FileStatus
{
    private BlockLocation[] locations;

    public LocatedFileStatus(FileStatus status, BlockLocation[] locations)
            throws IOException
    {
        super(status.getLen(),
                status.isDir(),
                status.getReplication(),
                status.getBlockSize(),
                status.getModificationTime(),
                status.getAccessTime(),
                status.getPermission(),
                status.getOwner(),
                status.getGroup(),
                status.getPath());
        this.locations = locations;
    }

    public BlockLocation[] getBlockLocations()
    {
        return locations;
    }
}

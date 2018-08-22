/*
 * WorldEdit, a Minecraft world manipulation toolkit
 * Copyright (C) sk89q <http://www.sk89q.com>
 * Copyright (C) WorldEdit team and contributors
 *
 * This program is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by the
 * Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License
 * for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package com.sk89q.worldedit.function.operation;

import static com.google.common.base.Preconditions.checkNotNull;

import com.sk89q.worldedit.MaxChangedBlocksException;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.extension.platform.Actor;
import com.sk89q.worldedit.extension.platform.Capability;

import javax.annotation.Nullable;

/**
 * Operation helper methods.
 */
public final class Operations {

    private Operations() {
    }

    private static RunContext BASIC_CONTEXT = new RunContext();

    /**
     * Complete a given operation synchronously until it completes.
     *
     * @param op operation to execute
     * @throws WorldEditException WorldEdit exception
     */
    public static void complete(Operation op) throws WorldEditException {
        while (op != null) {
            op = op.resume(BASIC_CONTEXT);
        }
    }

    /**
     * Complete a given operation synchronously until it completes. Catch all
     * errors that is not {@link MaxChangedBlocksException} for legacy reasons.
     *
     * @param op operation to execute
     * @throws MaxChangedBlocksException thrown when too many blocks have been changed
     */
    public static void completeLegacy(Operation op) throws MaxChangedBlocksException {
        while (op != null) {
            try {
                op = op.resume(BASIC_CONTEXT);
            } catch (MaxChangedBlocksException e) {
                throw e;
            } catch (WorldEditException e) {
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * Complete a given operation synchronously until it completes. Re-throw all
     * {@link WorldEditException} exceptions as
     * {@link RuntimeException}s.
     *
     * @param op operation to execute
     */
    public static void completeBlindly(Operation op) {
        while (op != null) {
            try {
                op = op.resume(BASIC_CONTEXT);
            } catch (WorldEditException e) {
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * Complete a given operation in a queue until it completes.
     *
     * @param op operation to execute
     * @param actor The actor to complete this with, or null for console
     */
    public static void completeQueued(Operation op, @Nullable Actor actor) {
        checkNotNull(op);

        if (actor == null) {
            actor = WorldEdit.getInstance().getPlatformManager().queryCapability(Capability.USER_COMMANDS).getConsoleCommandSender();
        }

        WorldEdit.getInstance().getSessionManager().get(actor).enqeueOperation(actor, op);
    }

}

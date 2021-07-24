package com.smart.server.tcp.channel;

import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

import com.google.common.collect.Multimaps;
import com.google.common.collect.SetMultimap;
import com.google.common.collect.Sets;
import com.smart.biz.common.model.em.MsgTypeEnum;

import io.netty.channel.Channel;
import io.netty.util.AttributeKey;

/**
 * @author chenjunlong
 */
public class ChannelRegistry {

    /**
     * uid -> channel
     * 存储uid和channel的映射关系
     */
    private static final ConcurrentHashMap<Long, Channel> userChannel = new ConcurrentHashMap<>();

    /**
     * roomId -> uid
     * 存储房间和用户的映射关系
     */
    private static final SetMultimap<String, Long> roomUser =
            Multimaps.synchronizedSetMultimap(Multimaps.newSetMultimap(new HashMap<>(), CopyOnWriteArraySet::new));

    /**
     * 记录当前连接数
     */
    private static AtomicLong connections = new AtomicLong(0L);

    /**
     * 用户Channel注册到房间
     *
     * @param roomId 房间ID
     * @param uid 用户ID
     * @param channel 连接
     */
    public static void add(String roomId, Long uid, Channel channel) {
        ChannelAttribute.add(channel, roomId, uid);
        roomUser.put(roomId, uid);
        userChannel.put(uid, channel);
        connections.incrementAndGet();
    }

    /**
     * 用户Channel从房间移除
     *
     * @param roomId 房间ID
     * @param uid 用户ID
     * @param channel 连接
     */
    public static void remove(String roomId, Long uid, Channel channel) {
        if (null == roomId || null == uid) {
            return;
        }
        ChannelAttribute.remove(channel);
        roomUser.remove(roomId, uid);
        userChannel.remove(uid, channel);
        connections.decrementAndGet();

        channel.disconnect();
        channel.close();
    }

    /**
     * 根据房间ID获取用户列表
     *
     * @param roomId 房间ID
     * @return 用户ID列表
     */
    public static Set<Long> getUidByRoomId(String roomId) {
        return roomUser.get(roomId);
    }

    /**
     * 获取所用房间用户
     *
     * @return 用户ID列表
     */
    public static Set<Long> getAllUid() {
        return roomUser.values().stream().collect(Collectors.toSet());
    }

    /**
     * 获取房间用户
     *
     * @param receiveId
     * @param msgType
     * @return
     */
    public static Set<Long> getUids(String receiveId, int msgType) {
        if (msgType == MsgTypeEnum.BOARD_CAST.getType()) {
            return ChannelRegistry.getAllUid();
        } else if (msgType == MsgTypeEnum.ROOM.getType()) {
            return ChannelRegistry.getUidByRoomId(receiveId);
        } else {
            return Sets.newHashSet(Long.parseLong(receiveId));
        }
    }

    /**
     * 根据uid获取连接
     *
     * @param uid 用户ID
     * @return 连接
     */
    public static Channel getChannelByUid(long uid) {
        return userChannel.get(uid);
    }

    /**
     * 获取连接数
     *
     * @return
     */
    public static long getConnections() {
        return connections.get();
    }

    public static class ChannelAttribute {

        private static final String UID_STR = "uid";
        private static final String ROOM_ID_STR = "roomId";
        private static final AttributeKey<Long> UID = AttributeKey.valueOf(UID_STR);
        private static final AttributeKey<String> ROOM_ID = AttributeKey.valueOf(ROOM_ID_STR);

        public static void add(Channel channel, String roomId, Long uid) {
            channel.attr(ROOM_ID).set(roomId);
            channel.attr(UID).set(uid);
        }

        public static void remove(Channel channel) {
            channel.attr(ROOM_ID).set("");
            channel.attr(UID).set(0L);
        }

        public static Long getUid(Channel channel) {
            return channel.attr(UID).get();
        }

        public static String getRoomId(Channel channel) {
            return channel.attr(ROOM_ID).get();
        }

        public static String getClientIp(Channel channel) {
            return ((InetSocketAddress) channel.remoteAddress()).getAddress().getHostAddress();
        }

        public static int getPort(Channel channel) {
            return ((InetSocketAddress) channel.remoteAddress()).getPort();
        }
    }

}

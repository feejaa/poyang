package org.feejaa.poyang.server;

import io.vertx.core.Vertx;
import lombok.extern.slf4j.Slf4j;
import org.feejaa.poyang.server.tcp.TcpServerHandler;

/**
 * Vertx HTTP 服务器
 *
 * @author <a href="https://github.com/liyupi">程序员鱼皮</a>
 * @learn <a href="https://codefather.cn">编程宝典</a>
 * @from <a href="https://yupi.icu">编程导航知识星球</a>
 */
@Slf4j
public class VertxServer implements Server {

    /**
     * 启动服务器
     *
     * @param port
     */
    public void doStart(int port) {
        // 创建 Vert.x 实例
        Vertx vertx = Vertx.vertx();

        // 创建 HTTP 服务器
        io.vertx.core.http.HttpServer server = vertx.createHttpServer();

        // 处理请求
        server.requestHandler(request -> {
            new TcpServerHandler();
        });

            // 启动 HTTP 服务器并监听指定端口
        server.listen(port, result -> {
            if (result.succeeded()) {
                log.info("Server is now listening on port {}", port);
            } else {
                log.info("Failed to start server: {}", String.valueOf(result.cause()));
            }
        });
    }
}

//
//  ========================================================================
//  Copyright (c) 1995-2017 Mort Bay Consulting Pty. Ltd.
//  ------------------------------------------------------------------------
//  All rights reserved. This program and the accompanying materials
//  are made available under the terms of the Eclipse Public License v1.0
//  and Apache License v2.0 which accompanies this distribution.
//
//      The Eclipse Public License is available at
//      http://www.eclipse.org/legal/epl-v10.html
//
//      The Apache License v2.0 is available at
//      http://www.opensource.org/licenses/apache2.0.php
//
//  You may elect to redistribute this code under either of these licenses.
//  ========================================================================
//

package com.kevin.es.crawl;

import com.kevin.es.domain.BankData;
import com.kevin.es.es.EsOperater;
import org.codehaus.jackson.map.ObjectMapper;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.AbstractHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

public class EmbedServer {
    public static void main(String[] args) throws Exception {
        Server server = new Server(9080);
        server.setHandler(new SearchHandler());

        server.start();
        server.join();
    }

    public static class SearchHandler extends AbstractHandler {
        private EsOperater es;
        public SearchHandler(){
            es = new EsOperater();
            es.open();
        }
        public void handle(String target,
                           Request baseRequest,
                           HttpServletRequest request,
                           HttpServletResponse response) throws IOException,
                ServletException {
            response.setContentType("text/html; charset=utf-8");
            response.setStatus(HttpServletResponse.SC_OK);

            PrintWriter out = response.getWriter();
            String sQuery = "";

            System.out.println(target);
            if(target.startsWith("/search/")){
                sQuery = target.replace("/search/", "");
                out.println(search(sQuery));
            } else if(target.startsWith("/id/")){
                sQuery = target.replace("/id/", "");
                out.println(queryById(sQuery));
            }
            baseRequest.setHandled(true);
            out.close();
        }

        private String search(String sQuery){
            String jsonlist = "";
            List<BankData> bankDataList = es.search(sQuery);
            ObjectMapper mapper = new ObjectMapper();
            try {
                jsonlist = mapper.writeValueAsString(bankDataList);

            } catch (IOException e) {
                e.printStackTrace();
            }
            return jsonlist;
        }

        private String queryById(String dataId){
            String json = "";
            BankData bankData = es.queryById(dataId);
            ObjectMapper mapper = new ObjectMapper();
            try {
                json = mapper.writeValueAsString(bankData);

            } catch (IOException e) {
                e.printStackTrace();
            }
            return json;
        }

    }
}
/*
 * Licensed to the Sakai Foundation (SF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The SF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package org.sakaiproject.kernel.user.servlet;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.servlets.HtmlResponse;
import org.apache.sling.jackrabbit.usermanager.impl.post.DeleteAuthorizableServlet;
import org.apache.sling.servlets.post.Modification;
import org.sakaiproject.kernel.user.UserPostProcessor;

import java.util.List;

import javax.jcr.RepositoryException;
import javax.servlet.http.HttpServletResponse;

/**
 * Sling Post Operation implementation for deleting one or more users and/or groups from the 
 * jackrabbit UserManager.

 * <h2>Rest Service Description</h2>
 * <p>
 * Deletes an Authorizable, currently a user or a group. Maps on to nodes of resourceType <code>sling/users</code> or <code>sling/users</code> like
 * <code>/rep:system/rep:userManager/rep:users</code> or <code>/rep:system/rep:userManager/rep:groups</code> mapped to a resource url
 * <code>/system/userManager/user</code> or <code>/system/userManager/group</code>. This servlet responds at
 * <code>/system/userManager/user.delete.html</code> or <code>/system/userManager/group.delete.html</code>.
 * The servlet also responds to single delete requests eg <code>/system/userManager/group/newGroup.delete.html</code>
 * </p>
 * <h4>Methods</h4>
 * <ul>
 * <li>POST</li>
 * </ul>
 * <h4>Post Parameters</h4>
 * <dl>
 * <dt>:applyTo</dt>
 * <dd>An array of relative resource references to Authorizables to be deleted, if this parameter is present, the url is ignored and all the Authorizables in the list are removed.</dd>
 * </dl>
 * <h4>Response</h4>
 * <dl>
 * <dt>200</dt>
 * <dd>Success, no body.</dd>
 * <dt>404</dt>
 * <dd>The resource was not found</dd>
 * <dt>500</dt>
 * <dd>Failure</dd>
 * </dl>
 * <h4>Example</h4>
 * 
 * <code>
 * curl -Fgo=1 http://localhost:8080/system/userManager/user/ieb.delete.html
 * </code>
 * 
 * 
 * @scr.component metatype="no" immediate="true"
 * @scr.service interface="javax.servlet.Servlet"
 * @scr.property name="sling.servlet.resourceTypes" values.0="sling/user" values.1="sling/group" values.2="sling/userManager"
 * @scr.property name="sling.servlet.methods" value="POST" 
 * @scr.property name="sling.servlet.selectors" value="delete" 
 * 
 * @scr.reference name="UserPostProcessor" bind="bindUserPostProcessor" unbind="unbindUserPostProcessor"
 *                interface="org.sakaiproject.kernel.user.UserPostProcessor"
 *
 */

public class DeleteSakaiAuthorizableServlet extends DeleteAuthorizableServlet {

  /**
   *
   */
  private static final long serialVersionUID = 3417673949322305891L;

  private UserPostProcessor userPostProcessor;

  /**
   * {@inheritDoc}
   * @see org.apache.sling.jackrabbit.usermanager.post.CreateUserServlet#handleOperation(org.apache.sling.api.SlingHttpServletRequest, org.apache.sling.api.servlets.HtmlResponse, java.util.List)
   */
  @Override
  protected void handleOperation(SlingHttpServletRequest request, HtmlResponse response,
      List<Modification> changes) throws RepositoryException {
    super.handleOperation(request, response, changes);
    try {
      userPostProcessor.process(request, changes);
    } catch (Exception e) {
      response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
      return;
    }
  }
  
  /**
   * @param userPostProcessor the userPostProcessor to set
   */
  protected void bindUserPostProcessor(UserPostProcessor userPostProcessor) {
    this.userPostProcessor = userPostProcessor;
  }

  /**
   * @param userPostProcessor the userPostProcessor to set
   */
  protected void unbindUserPostProcessor(UserPostProcessor userPostProcessor) {
    this.userPostProcessor = null;
  }


}

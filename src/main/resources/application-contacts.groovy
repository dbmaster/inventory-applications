import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import com.branegy.service.core.QueryRequest;
import com.branegy.inventory.api.ContactLinkService;
import com.branegy.inventory.api.InventoryService;
import com.branegy.inventory.model.Application;
import com.branegy.inventory.model.ContactLink;
import com.branegy.inventory.model.Server;
import com.branegy.service.base.api.ProjectService;

import net.sf.jasperreports.engine.JRResultSetDataSource;
import net.sf.jasperreports.engine.JRField;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource

def toURL = { link -> link.encodeURL().replaceAll("\\+", "%20") }
String.metaClass.encodeURL = { java.net.URLEncoder.encode(delegate) }


def contactLinks = dbm.getService(ContactLinkService.class).findAllByClass(Application.class,null)
def roles = contactLinks.collect { it.getCustomData(ContactLink.ROLE) }.unique().sort()

println """
 <table class="simple-table" cellspacing="0" cellpadding="10">
 <tr style="background-color:#EEE">
    <td>Application</td>${roles.collect{ "<td>" + (it == null ? "undefined" : it) + "</td>" }.join("")}
 </tr>"""

applications = dbm.getService(InventoryService.class).getApplicationList(new QueryRequest())
applications.sort { it.getApplicationName().toUpperCase() }

appContacts = contactLinks.groupBy { it.getApplication() }

String projectName =  dbm.getService(ProjectService.class).getCurrentProject().getName();

applications.each { app ->
    String objectName, objectType, objectLink, contactLink

    objectName = app.getApplicationName();
    objectType = "Application"
    objectLink = "#inventory/project:${toURL(projectName)}/applications/application:${toURL(objectName)}/contacts"


    println """<tr><td><a href='${objectLink}'>${objectName}</a></td>"""
    roles.each { roleName ->
                    println "<td>"
                    appContacts[app].each { link ->
                            appRole = link.getCustomData(ContactLink.ROLE)
                            if ((appRole==null && roleName==null) ||
                                (appRole!=null && roleName!=null && appRole.equals(roleName)))
                            {
                                contactName = link.getContact().getContactName();
                                contactLink = "#inventory/project:${toURL(projectName)}/contacts/contact:${toURL(contactName)}"
                                print "<a href='${contactLink}'>${contactName}</a> &nbsp;&nbsp; "
                            }
                    }
                    print "</td>"
    }
    println "</tr>"
}

println "</table>"
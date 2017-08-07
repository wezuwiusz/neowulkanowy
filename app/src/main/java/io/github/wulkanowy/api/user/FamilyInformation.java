package io.github.wulkanowy.api.user;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import io.github.wulkanowy.api.StudentAndParent;
import io.github.wulkanowy.api.Vulcan;
import io.github.wulkanowy.api.login.LoginErrorException;

public class FamilyInformation extends Vulcan {

    private StudentAndParent snp;

    private String studentDataPageUrl = "Uczen.mvc/DanePodstawowe";

    public FamilyInformation(StudentAndParent snp) throws IOException, LoginErrorException {
        this.snp = snp;
    }

    public List<FamilyMember> getFamilyMembers() throws IOException, LoginErrorException {
        Elements membersElements = snp.getSnPPageDocument(studentDataPageUrl)
                .select(".mainContainer > article:nth-of-type(n+4)");

        List<FamilyMember> familyMembers = new ArrayList<>();

        for (Element e : membersElements) {
            familyMembers.add(new FamilyMember()
                    .setName(snp.getRowDataChildValue(e, 1))
                    .setKinship(snp.getRowDataChildValue(e, 2))
                    .setAddress(snp.getRowDataChildValue(e, 3))
                    .setTelephones(snp.getRowDataChildValue(e, 4))
                    .setEmail(snp.getRowDataChildValue(e, 5))
            );
        }

        return familyMembers;
    }
}

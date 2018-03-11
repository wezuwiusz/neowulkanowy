package io.github.wulkanowy.api.user;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import io.github.wulkanowy.api.SnP;
import io.github.wulkanowy.api.VulcanException;

public class FamilyInformation {

    private static final String STUDENT_DATA_PAGE_URL = "Uczen.mvc/DanePodstawowe";

    private SnP snp;

    public FamilyInformation(SnP snp) {
        this.snp = snp;
    }

    public List<FamilyMember> getFamilyMembers() throws IOException, VulcanException {
        Elements membersElements = snp.getSnPPageDocument(STUDENT_DATA_PAGE_URL)
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

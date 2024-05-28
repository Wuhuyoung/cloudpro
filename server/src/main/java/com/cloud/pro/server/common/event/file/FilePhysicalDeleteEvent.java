//package com.cloud.pro.server.common.stream.event.file;
//
//import com.cloud.pro.server.modules.entity.UserFile;
//import lombok.EqualsAndHashCode;
//import lombok.Getter;
//import lombok.Setter;
//import lombok.ToString;
//import org.springframework.context.ApplicationEvent;
//
//import java.util.List;
//
///**
// * 文件被物理删除的事件
// */
//@Getter
//@Setter
//@EqualsAndHashCode
//@ToString
//public class FilePhysicalDeleteEvent extends ApplicationEvent {
//
//    private static final long serialVersionUID = -6598514575940945788L;
//    /**
//     * 所有被物理删除的文件实体
//     */
//    private List<UserFile> allRecords;
//
//    public FilePhysicalDeleteEvent(Object source, List<UserFile> allRecords) {
//        super(source);
//        this.allRecords = allRecords;
//    }
//}

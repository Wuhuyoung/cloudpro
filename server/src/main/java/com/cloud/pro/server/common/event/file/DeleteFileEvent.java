//package com.cloud.pro.server.common.stream.event.file;
//
//import lombok.EqualsAndHashCode;
//import lombok.Getter;
//import lombok.Setter;
//import lombok.ToString;
//import org.springframework.context.ApplicationEvent;
//
//import java.util.List;
//
///**
// * 文件删除事件
// */
//@Getter
//@Setter
//@EqualsAndHashCode
//@ToString
//public class DeleteFileEvent extends ApplicationEvent {
//    private static final long serialVersionUID = 1056062250877710499L;
//
//    private List<Long> fileIdList;
//
//    public DeleteFileEvent(Object source, List<Long> fileIdList) {
//        super(source);
//        this.fileIdList = fileIdList;
//    }
//}

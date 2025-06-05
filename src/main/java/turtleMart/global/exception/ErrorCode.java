package turtleMart.global.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    // 출고지(물류센터) 관련
    SENDER_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 출고지(물류센터)입니다."),
    SENDER_ALREADY_EXISTS(HttpStatus.CONFLICT, "이미 존재하는 출고지(물류센터)입니다."),

    // 택배사 관련
    COURIER_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 택배사입니다."),
    COURIER_ALREADY_EXISTS(HttpStatus.CONFLICT, "이미 존재하는 택배사입니다."),
    COURIER_DELETE_FAILED(HttpStatus.CONFLICT, "계약된 출고지(물류센터)가 존재하기 때문에 삭제할 수 없습니다."),

    // 배송 관련
    DELIVERY_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 배송입니다."),
    INVALID_DELIVERY_STATUS(HttpStatus.BAD_REQUEST, "허용되지 않은 상태 변경입니다."),

    // 판매자 관련
    SELLER_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 판매자입니다."),

    // 주소 관련
    ADDRESS_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 주소입니다."),

    //권한 관련
    FORBIDDEN(HttpStatus.FORBIDDEN,"접근권한이 없습니다."),

    //상품 관련
    PRODUCT_NOT_FOUND(HttpStatus.NOT_FOUND,"존재하지 않는 상품입니다."),
    PRODUCT_ALL_READY_SURVIVE(HttpStatus.BAD_REQUEST, "해당 상품은 삭제되지않았습니다"),
    PRODUCT_OPTION_GROUP_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 상품옵션그룹입니다."),
    PRODUCT_OPTION_VALUE_NOT_FOUND(HttpStatus.NOT_FOUND, "상품옵션그룹에 존재하지않는 상품옵션값입니다."),
    PRODUCT_OPTION_COMBINATION_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지않는 상품옵션 조합입니다."),
    PRODUCT_OPTION_COMBINATION_ALL_READY_SOLD(HttpStatus.BAD_REQUEST, "주문이 존재하는 상품옵션조합은 삭제할수없습니다."),
    PRODUCT_NOT_BELONG_TO_SELLER(HttpStatus.FORBIDDEN, "상품이 판매자 소유가 아닙니다."),
    REQUEST_OPTION_VALUE_NOT_FOUND(HttpStatus.BAD_REQUEST, "" ), /*TODO 성우님 대신 임시로 만들어둔 에러코드입니다.*/
    REQUEST_OPTION_GROUP_NOT_FOUND(HttpStatus.NOT_FOUND, "요청한 상품옵션그룹이 존재하지 않습니다."),

    //주문 관련
    ORDER_SHEET_NOT_FOUND(HttpStatus.NOT_FOUND, "주문서 내용이 존재하지 않습니다."),
    ORDER_NOT_FOUND(HttpStatus.NOT_FOUND, "주문 내역이 존재하지 않습니다."),
    ORDER_ITEM_NOT_FOUND(HttpStatus.NOT_FOUND, "상품 주문 내역이 존재하지 않습니다."),
    ORDER_ITEM_NOT_IN_ORDER(HttpStatus.BAD_REQUEST, "해당 주문에 속하지 않은 상품입니다."),
    ORDER_ITEM_NOT_OWNED_BY_MEMBER(HttpStatus.FORBIDDEN, "해당 회원이 이 주문 항목의 소유자가 아닙니다."),
    NO_REFUNDING_ORDER_ITEM_FOUND(HttpStatus.NOT_FOUND, "환불중인 주문 항목이 없습니다."),
    ORDER_PRICE_VALIDATION_FAILED(HttpStatus.CONFLICT, "주문 생성 중 가격 정합성 오류: 주문서 가격과 상품 가격이 일치하지 않습니다."),

    //장바구니 관련
    PRODUCT_NOT_IN_CART(HttpStatus.NOT_FOUND, "장바구니에 삭제하려는 상품이 존재하지 않음"),

    //유저 관련
    MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 유저입니다."),
    //여러가지 관련
    TIME_OUT(HttpStatus.INTERNAL_SERVER_ERROR, "응답 대기시간을 초과하였습니다."),
    INTERRUPT(HttpStatus.INTERNAL_SERVER_ERROR, "인터럽"),

    //리뷰관련
    PRODUCT_REVIEW_TEMPLATE_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 상품의 리뷰템플릿입니다."),
    REASON_CODE_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 신고코드입니다."),
    REVIEW_REPORT_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 리뷰 신고 건입니다."),
    REVIEW_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 리뷰입니다"),
    REVIEW_TEMPLATE_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지않는 리뷰 템플릿입니다."),
    DUPLICATE_TEMPLATE_SELECTION(HttpStatus.BAD_REQUEST, "같은 리뷰템플릿을 중복 선택할 수 없습니다."),
    ALREADY_DELETED_REASON_CODE(HttpStatus.BAD_REQUEST, "이미 삭제된 신고 코드입니다."),
    DUPLICATE_REVIEW_REPORT(HttpStatus.CONFLICT, "하나의 주문건에 대한 신고는 한번만 가능합니다."),
    REVIEW_NOT_ALLOWED_BEFORE_CONFIRMATION(HttpStatus.BAD_REQUEST, "주문확정된 상품만 리뷰작성이 가능합니다."),
    ALREADY_DELETED_REVIEW_TEMPLATE(HttpStatus.CONFLICT, "이미 삭제된 리뷰 템플릿입니다."),
    REVIEW_ALREADY_EXISTS(HttpStatus.CONFLICT, "주문건에 대한 리뷰는 한번만 작성가능합니다."),

    //이미지 관련
    MINIO_INITIALIZATION_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "버킷 설정 중 문제 발생"),
    IMAGE_UPLOAD_FAILED(HttpStatus.BAD_GATEWAY, "이미지 업로드 중 문제가 발생하였습니다"),
    IMAGE_VIEW_FAILED(HttpStatus.BAD_GATEWAY, "이미지를 불러오는 도중 문제가 발생했습니다"),
    SOFT_LOCK_CANT_ACCESS(HttpStatus.CONFLICT, "현재 다른요청을 처리중임으로 접근할수없습니다."),

    //엘라스틱서치 관련
    SEARCH_ERROR_RETRY_LATER(HttpStatus.INTERNAL_SERVER_ERROR, "검색 중 알수 없는 문제가 발생하였습니다. 잠시후 다시 시도해 주세요");


    private final HttpStatus httpStatus;
    private final String message;
}

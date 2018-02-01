package kz.greetgo.sandbox.stand.stand_register_impls;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import kz.greetgo.depinject.core.Bean;
import kz.greetgo.depinject.core.BeanGetter;
import kz.greetgo.msoffice.xlsx.gen.Align;
import kz.greetgo.msoffice.xlsx.gen.NumFmt;
import kz.greetgo.msoffice.xlsx.gen.Sheet;
import kz.greetgo.msoffice.xlsx.gen.Xlsx;
import kz.greetgo.mvc.interfaces.RequestTunnel;
import kz.greetgo.sandbox.controller.errors.InvalidParameter;
import kz.greetgo.sandbox.controller.errors.NotFound;
import kz.greetgo.sandbox.controller.model.*;
import kz.greetgo.sandbox.controller.register.ClientRegister;
import kz.greetgo.sandbox.controller.register.TempSessionRegister;
import kz.greetgo.sandbox.controller.util.Util;
import kz.greetgo.sandbox.db.stand.beans.StandDb;
import kz.greetgo.sandbox.db.stand.model.CharmDot;
import kz.greetgo.sandbox.db.stand.model.ClientDot;
import kz.greetgo.sandbox.db.stand.model.PersonDot;
import kz.greetgo.sandbox.stand.launchers.LaunchStandServer;
import kz.greetgo.sandbox.stand.util.PageUtils;

import java.io.OutputStream;
import java.net.URL;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Bean
public class ClientRegisterStand implements ClientRegister {

  public BeanGetter<StandDb> db;
  public BeanGetter<TempSessionRegister> tempSessionRegister;

  @Override
  public long getCount(ClientRecordRequest request) {
    List<ClientDot> clientDots = new ArrayList<>(db.get().clientStorage.values());
    clientDots = this.getFilteredList(clientDots, request.nameFilter);

    return clientDots.size();
  }

  @Override
  public List<ClientRecord> getRecordList(ClientRecordRequest request) {
    List<ClientDot> clientDots = new ArrayList<>(db.get().clientStorage.values());
    List<ClientRecord> clientRecords = new ArrayList<>();

    clientDots = this.getFilteredList(clientDots, request.nameFilter);
    clientDots = this.getSortedList(clientDots, request.columnSortType, request.sortAscend);

    PageUtils.cutPage(clientDots,
      request.clientRecordCountToSkip,
      request.clientRecordCount);

    for (ClientDot clientDot : clientDots)
      clientRecords.add(clientDot.toClientRecord());

    return clientRecords;
  }

  private List<ClientDot> getFilteredList(List<ClientDot> clientDots, String nameFilter) {
    if (nameFilter == null || nameFilter.length() == 0)
      return clientDots;

    String loweredNameFilter = nameFilter.toLowerCase();

    Stream<ClientDot> stream = clientDots.stream().filter(new Predicate<ClientDot>() {
      @Override
      public boolean test(ClientDot clientDot) {
        if (clientDot.surname.toLowerCase().contains(loweredNameFilter) ||
          clientDot.name.toLowerCase().contains(loweredNameFilter) ||
          clientDot.patronymic.toLowerCase().contains(loweredNameFilter))
          return true;

        return false;
      }
    });

    clientDots = stream.collect(Collectors.toList());

    return clientDots;
  }

  private List<ClientDot> getSortedList(List<ClientDot> clientDots, ColumnSortType columnSortType, boolean sortAscend) {
    switch (columnSortType) {
      case AGE:
        clientDots = this.getListByAge(clientDots, sortAscend);
        break;
      case TOTALACCOUNTBALANCE:
        clientDots = this.getListByTotalAccountBalance(clientDots, sortAscend);
        break;
      case MAXACCOUNTBALANCE:
        clientDots = this.getListByMaxAccountBalance(clientDots, sortAscend);
        break;
      case MINACCOUNTBALANCE:
        clientDots = this.getListByMinAccountBalance(clientDots, sortAscend);
        break;
      default:
        clientDots = this.getDefaultList(clientDots);
    }

    return clientDots;
  }

  private List<ClientDot> getDefaultList(List<ClientDot> clientDots) {
    return clientDots;
  }

  private List<ClientDot> getListByAge(List<ClientDot> clientDots, boolean ascend) {
    //TODO: lambda + Integer.comparator?
    if (ascend) {
      clientDots.sort(new Comparator<ClientDot>() {
        public int compare(ClientDot o1, ClientDot o2) {
          return o1.age - o2.age;
        }
      });
    } else {
      clientDots.sort(new Comparator<ClientDot>() {
        public int compare(ClientDot o1, ClientDot o2) {
          return o2.age - o1.age;
        }
      });
    }

    return clientDots;
  }

  private List<ClientDot> getListByTotalAccountBalance(List<ClientDot> clientDots, boolean ascend) {
    //TODO: lambda + Integer.comparator?
    if (ascend) {
      clientDots.sort(new Comparator<ClientDot>() {
        public int compare(ClientDot o1, ClientDot o2) {
          return Float.compare(Util.stringToFloat(o1.totalAccountBalance), Util.stringToFloat(o2.totalAccountBalance));
        }
      });
    } else {
      clientDots.sort(new Comparator<ClientDot>() {
        public int compare(ClientDot o1, ClientDot o2) {
          return Float.compare(Util.stringToFloat(o2.totalAccountBalance), Util.stringToFloat(o1.totalAccountBalance));
        }
      });
    }

    return clientDots;
  }

  private List<ClientDot> getListByMaxAccountBalance(List<ClientDot> clientDots, boolean ascend) {
    //TODO: lambda + Integer.comparator?
    if (ascend) {
      clientDots.sort(new Comparator<ClientDot>() {
        public int compare(ClientDot o1, ClientDot o2) {
          return Float.compare(Util.stringToFloat(o1.maxAccountBalance), Util.stringToFloat(o2.maxAccountBalance));
        }
      });
    } else {
      clientDots.sort(new Comparator<ClientDot>() {
        public int compare(ClientDot o1, ClientDot o2) {
          return Float.compare(Util.stringToFloat(o2.maxAccountBalance), Util.stringToFloat(o1.maxAccountBalance));
        }
      });
    }

    return clientDots;
  }

  private List<ClientDot> getListByMinAccountBalance(List<ClientDot> clientDots, boolean ascend) {
    //TODO: lambda + Integer.comparator?
    if (ascend) {
      clientDots.sort(new Comparator<ClientDot>() {
        public int compare(ClientDot o1, ClientDot o2) {
          return Float.compare(Util.stringToFloat(o1.minAccountBalance), Util.stringToFloat(o2.minAccountBalance));
        }
      });
    } else {
      clientDots.sort(new Comparator<ClientDot>() {
        public int compare(ClientDot o1, ClientDot o2) {
          return Float.compare(Util.stringToFloat(o2.minAccountBalance), Util.stringToFloat(o1.minAccountBalance));
        }
      });
    }

    return clientDots;
  }

  @Override
  public void removeRecord(long id) {
    Map<Long, ClientDot> clientDotMap = db.get().clientStorage;

    if (clientDotMap.remove(id) == null)
      throw new NotFound();
  }

  @Override
  public ClientDetails getDetails(Long id) {
    List<CharmDot> charmDots = new ArrayList<>(db.get().charmStorage.values());
    ClientDetails clientDetails;

    if (id == null) {
      clientDetails = new ClientDetails();

      clientDetails.id = null;
      clientDetails.surname = "";
      clientDetails.name = "";
      clientDetails.patronymic = "";
      clientDetails.gender = Gender.EMPTY;
      clientDetails.birthdate = "";
      clientDetails.charmId = charmDots.get(0).toCharm().id;

      clientDetails.registrationAddressInfo = new AddressInfo();
      clientDetails.registrationAddressInfo.type = AddressType.REGISTRATION;
      clientDetails.registrationAddressInfo.street = "";
      clientDetails.registrationAddressInfo.house = "";
      clientDetails.registrationAddressInfo.flat = "";

      clientDetails.factualAddressInfo = new AddressInfo();
      clientDetails.factualAddressInfo.type = AddressType.REGISTRATION;
      clientDetails.factualAddressInfo.street = "";
      clientDetails.factualAddressInfo.house = "";
      clientDetails.factualAddressInfo.flat = "";

      clientDetails.phones = new ArrayList<>();
    } else {
      ClientDot clientDot = db.get().clientStorage.get(id);
      clientDetails = clientDot.toClientDetails();
    }

    for (CharmDot charmDot : charmDots)
      clientDetails.charmList.add(charmDot.toCharm());

    return clientDetails;
  }

  @Override
  public void saveDetails(ClientDetailsToSave detailsToSave) {
    Map<Long, ClientDot> clientDotMap = db.get().clientStorage;
    ClientDot clientDot;
    long id = db.get().curClientId.getAndIncrement();
    db.get().curClientId.set(id + 1);

    if (detailsToSave.id == null) {
      clientDot = new ClientDot();
      clientDot.toClientDot(detailsToSave, id, db.get().charmStorage);
      clientDotMap.put(id, clientDot);
    } else {
      clientDot = clientDotMap.get(detailsToSave.id);
      clientDot.toClientDot(detailsToSave, null, db.get().charmStorage);
    }
  }

  @Override
  public String prepareRecordListStream(String personId) {
    return tempSessionRegister.get().save(personId, "/client/list/report", 60);
  }

  @Override
  public void streamRecordList(String token, OutputStream outStream, ClientRecordRequest request,
                               FileContentType fileContentType, RequestTunnel requestTunnel) throws Exception {
    tempSessionRegister.get().checkForValidity(token, requestTunnel.getTarget());
    String personId = tempSessionRegister.get().remove(token);

    String contentType, fileType;
    switch (fileContentType) {
      case PDF:
        contentType = "application/pdf";
        fileType = "pdf";
        break;
      case XLSX:
        contentType = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
        fileType = "xlsx";
        break;
      default:
        throw new InvalidParameter();
    }
    requestTunnel.setResponseContentType(contentType);
    requestTunnel.setResponseHeader("Content-Disposition", "attachment; filename=\"client_records_" +
      LocalDateTime.now().format(DateTimeFormatter.ofPattern(Util.reportDatePattern)) + "." + fileType + "\"");

    List<ClientDot> clientDots = new ArrayList<>(db.get().clientStorage.values());
    clientDots = this.getFilteredList(clientDots, request.nameFilter);
    clientDots = this.getSortedList(clientDots, request.columnSortType, request.sortAscend);

    switch (fileContentType) {
      case PDF:
        this.streamRecordListToPdf(outStream, request, clientDots, personId);
        break;
      case XLSX:
        this.streamRecordListToXlsx(outStream, request, clientDots, personId);
        break;
    }
  }

  private void streamRecordListToXlsx(OutputStream outStream, ClientRecordRequest request, List<ClientDot> clientDots,
                                      String personId) {
    Xlsx document = new Xlsx();
    Sheet sheet = document.newSheet(true);

    int curCol = 1;
    sheet.setWidth(curCol++, 45f);
    sheet.setWidth(curCol++, 20f);
    sheet.setWidth(curCol++, 15f);
    sheet.setWidth(curCol++, 25f);
    sheet.setWidth(curCol++, 25f);
    sheet.setWidth(curCol, 25f);

    sheet.setDefaultRowHeight(20f);

    sheet.style().font().setSize(14);
    sheet.style().font().setItalic(true);
    sheet.row().start();
    sheet.cellStr(1, "Список клиентских записей  от " +
      LocalDateTime.now().format(DateTimeFormatter.ofPattern(Util.reportDatePattern)));
    sheet.row().finish();
    sheet.style().font().setItalic(false);

    sheet.style().font().setSize(14);
    sheet.row().start();
    PersonDot personDot = db.get().personStorage.get(personId);
    sheet.cellStr(1, "Сформирован для пользователя: " +
      Util.getFullname(personDot.surname, personDot.name, personDot.patronymic));
    sheet.row().finish();
    sheet.skipRow();

    sheet.style().alignment().setHorizontal(Align.center);
    sheet.style().font().setSize(12);
    sheet.style().font().setBold(true);
    curCol = 1;
    sheet.row().start();
    sheet.cellStr(curCol++, "ФИО");
    sheet.cellStr(curCol++, "Характер");
    if (request.columnSortType == ColumnSortType.AGE) {
      sheet.style().font().setItalic(true);
      sheet.cellStr(curCol++, "Возраст");
      sheet.style().font().setItalic(false);
    } else {
      sheet.cellStr(curCol++, "Возраст");
    }
    if (request.columnSortType == ColumnSortType.TOTALACCOUNTBALANCE) {
      sheet.style().font().setItalic(true);
      sheet.cellStr(curCol++, "Общий остаток счетов");
      sheet.style().font().setItalic(false);
    } else {
      sheet.cellStr(curCol++, "Общий остаток счетов");
    }
    if (request.columnSortType == ColumnSortType.MAXACCOUNTBALANCE) {
      sheet.style().font().setItalic(true);
      sheet.cellStr(curCol++, "Максимальный остаток");
      sheet.style().font().setItalic(false);
    } else {
      sheet.cellStr(curCol++, "Максимальный остаток");
    }
    if (request.columnSortType == ColumnSortType.MINACCOUNTBALANCE) {
      sheet.style().font().setItalic(true);
      sheet.cellStr(curCol, "Минимальный остаток");
      sheet.style().font().setItalic(false);
    } else {
      sheet.cellStr(curCol, "Минимальный остаток");
    }
    sheet.row().finish();
    sheet.style().font().setBold(false);

    sheet.style().alignment().setHorizontal(Align.left);
    sheet.style().font().setSize(14);
    for (ClientDot clientDot : clientDots) {
      curCol = 1;
      sheet.row().start();
      sheet.cellStr(curCol++, Util.getFullname(clientDot.surname, clientDot.name, clientDot.patronymic));
      sheet.cellStr(curCol++, clientDot.charm.name);
      sheet.cellInt(curCol++, clientDot.age);
      sheet.cellDouble(curCol++, Util.stringToFloat(clientDot.totalAccountBalance), NumFmt.NUM_SIMPLE2);
      sheet.cellDouble(curCol++, Util.stringToFloat(clientDot.maxAccountBalance), NumFmt.NUM_SIMPLE2);
      sheet.cellDouble(curCol, Util.stringToFloat(clientDot.minAccountBalance), NumFmt.NUM_SIMPLE2);
      sheet.row().finish();
    }

    document.complete(outStream);
  }

  private void streamRecordListToPdf(OutputStream outStream, ClientRecordRequest request, List<ClientDot> clientDots,
                                     String personId) throws Exception {
    URL resource = LaunchStandServer.class.getResource("/Roboto-Regular.ttf");
    String fontPath = Paths.get(resource.toURI()).toAbsolutePath().toString();
    BaseFont bf = BaseFont.createFont(fontPath, BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
    Font headerFont = new Font(bf, 10);
    Font defaultFont = new Font(bf, 10);

    Document document = new Document();
    PdfWriter.getInstance(document, outStream);
    document.open();

    document.add(this.paragraphBuilder("Список клиентских записей  от " +
      LocalDateTime.now().format(DateTimeFormatter.ofPattern(Util.reportDatePattern)), defaultFont, 10f));

    PersonDot personDot = db.get().personStorage.get(personId);
    document.add(this.paragraphBuilder("Сформирован для пользователя: " +
      Util.getFullname(personDot.surname, personDot.name, personDot.patronymic), defaultFont, 10f));

    float[] columnWidthWeights = {4, 4, 2, 3, 3, 3};
    PdfPTable table = new PdfPTable(columnWidthWeights);
    table.setWidthPercentage(100);

    table.addCell(this.pdfPCellDefault(new PdfPCell(new Phrase("ФИО", headerFont))));
    table.addCell(this.pdfPCellDefault(new PdfPCell(new Phrase("Характер", headerFont))));
    table.addCell(this.pdfPCellHeaderBuilder("Возраст",
      request.columnSortType == ColumnSortType.AGE, headerFont));
    table.addCell(this.pdfPCellHeaderBuilder("Общий остаток счетов",
      request.columnSortType == ColumnSortType.TOTALACCOUNTBALANCE, headerFont));
    table.addCell(this.pdfPCellHeaderBuilder("Максимальный остаток",
      request.columnSortType == ColumnSortType.MAXACCOUNTBALANCE, headerFont));
    table.addCell(this.pdfPCellHeaderBuilder("Минимальный остаток",
      request.columnSortType == ColumnSortType.MINACCOUNTBALANCE, headerFont));

    this.pdfPCellDefault(table.getDefaultCell()).setBackgroundColor(GrayColor.GRAYWHITE);
    for (ClientDot clientDot : clientDots) {
      table.addCell(new Phrase(Util.getFullname(clientDot.surname, clientDot.name, clientDot.patronymic), defaultFont));
      table.addCell(new Phrase(clientDot.charm.name, defaultFont));
      table.addCell(new Phrase(String.valueOf(clientDot.age), defaultFont));
      table.addCell(new Phrase(String.format(Util.floatFormat, Util.stringToFloat(clientDot.totalAccountBalance)), defaultFont));
      table.addCell(new Phrase(String.format(Util.floatFormat, Util.stringToFloat(clientDot.maxAccountBalance)), defaultFont));
      table.addCell(new Phrase(String.format(Util.floatFormat, Util.stringToFloat(clientDot.minAccountBalance)), defaultFont));
    }

    document.add(table);
    document.close();
  }

  private PdfPCell pdfPCellHeaderBuilder(String text, boolean sort, Font font) {
    PdfPCell cell = new PdfPCell(new Phrase(text, font));
    if (sort)
      cell.setBackgroundColor(new BaseColor(102, 144, 102));
    else
      cell.setBackgroundColor(new BaseColor(191, 191, 191));

    return this.pdfPCellDefault(cell);
  }

  private PdfPCell pdfPCellDefault(PdfPCell cell) {
    cell.setUseAscender(true);
    cell.setUseDescender(true);
    cell.setHorizontalAlignment(Element.ALIGN_CENTER);
    cell.setVerticalAlignment(Element.ALIGN_MIDDLE);

    return cell;
  }

  private Paragraph paragraphBuilder(String text, Font font, float spaceAfter) {
    Paragraph paragraph = new Paragraph(text, font);
    paragraph.setSpacingAfter(spaceAfter);
    return paragraph;
  }
}
